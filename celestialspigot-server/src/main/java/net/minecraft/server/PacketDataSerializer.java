package net.minecraft.server;

import com.google.common.base.Charsets;
import com.kaydeesea.spigot.CompactHacks;
import com.kaydeesea.spigot.network.VarIntUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufProcessor;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.UUID;
// PandaSpigot start
import io.netty.util.ByteProcessor;
import java.nio.channels.FileChannel;
// PandaSpigot end

import org.bukkit.craftbukkit.inventory.CraftItemStack; // CraftBukkit

public class PacketDataSerializer extends ByteBuf {

    private final ByteBuf a;

    // PandaSpigot start - large packet limit
    private static final int DEFAULT_LIMIT = Short.MAX_VALUE;
    private static final int LARGE_PACKET_LIMIT = Short.MAX_VALUE * 1024;
    private final boolean allowLargePackets;
    public PacketDataSerializer(ByteBuf bytebuf) {
        /*
         * By default, we limit the size of the received byte array to Short.MAX_VALUE, which is 31 KB.
         * However, we make an exception when ProtocolSupport is installed, to allow 1.7 clients to work,
         * and limit them to 31 MEGABYTES as they seem to need to send larger packets sometimes.
         * Although a 31 MB limit leaves the server slightly vulnerable,
         * it's still much better than the old system of having no limit,
         * which would leave the server vulnerable to packets up to 2 GIGABYTES in size.
         */
        this.allowLargePackets = CompactHacks.hasProtocolSupport();
    // PandaSpigot end
        this.a = bytebuf;
    }

    public static int a(int i) {
        for (int j = 1; j < 5; ++j) {
            if ((i & -1 << j * 7) == 0) {
                return j;
            }
        }

        return 5;
    }

    public void a(byte[] abyte) {
        this.b(abyte.length);
        this.writeBytes(abyte);
    }

    // Paper start
    public byte[] a() {
        return readByteArray(this.allowLargePackets ? LARGE_PACKET_LIMIT : DEFAULT_LIMIT); // PandaSpigot - large packet limit
    }

    public byte[]readByteArray(int limit) {
        int len = this.e();
        if (len > limit) throw new DecoderException("The received a byte array longer than allowed " + len + " > " + limit);
        byte[] abyte = new byte[len];
    // Paper end

        this.readBytes(abyte);
        return abyte;
    }

    public BlockPosition c() {
        return BlockPosition.fromLong(this.readLong());
    }

    public void a(BlockPosition blockposition) {
        this.writeLong(blockposition.asLong());
    }

    public IChatBaseComponent d() throws IOException {
        return IChatBaseComponent.ChatSerializer.a(this.c(32767));
    }

    public void a(IChatBaseComponent ichatbasecomponent) throws IOException {
        this.a(IChatBaseComponent.ChatSerializer.a(ichatbasecomponent));
    }

    public <T extends Enum<T>> T a(Class<T> oclass) {
        return ((T[]) oclass.getEnumConstants())[this.e()]; // CraftBukkit - fix decompile error
    }

    public void a(Enum<?> oenum) {
        this.b(oenum.ordinal());
    }

    public int e() {
        return VarIntUtil.readVarInt(this.a); // PandaSpigot - Optimize VarInt reading
    }

    public long f() {
        long i = 0L;
        int j = 0;

        byte b0;

        do {
            b0 = this.readByte();
            i |= (long) (b0 & 127) << j++ * 7;
            if (j > 10) {
                throw new RuntimeException("VarLong too big");
            }
        } while ((b0 & 128) == 128);

        return i;
    }

    public void a(UUID uuid) {
        this.writeLong(uuid.getMostSignificantBits());
        this.writeLong(uuid.getLeastSignificantBits());
    }

    public UUID g() {
        return new UUID(this.readLong(), this.readLong());
    }

    public void b(int i) {
        VarIntUtil.writeVarInt(this.a, i); // PandaSpigot - Optimize VarInt writing
    }

    public void b(long i) {
        while ((i & -128L) != 0L) {
            this.writeByte((int) (i & 127L) | 128);
            i >>>= 7;
        }

        this.writeByte((int) i);
    }

    public void a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound == null) {
            this.writeByte(0);
        } else {
            try {
                NBTCompressedStreamTools.a(nbttagcompound, (DataOutput) (new ByteBufOutputStream(this)));
            } catch (Exception ioexception) { // CraftBukkit - IOException -> Exception
                throw new EncoderException(ioexception);
            }
        }

    }

    public NBTTagCompound h() throws IOException {
        int i = this.readerIndex();
        byte b0 = this.readByte();

        if (b0 == 0) {
            return null;
        } else {
            this.readerIndex(i);
            return NBTCompressedStreamTools.a((DataInput) (new ByteBufInputStream(this)), new NBTReadLimiter(50000L)); // PandaSpigot - Reduce NBT Read Limiter
        }
    }

    public void a(ItemStack itemstack) {
        if (itemstack == null || itemstack.getItem() == null) { // CraftBukkit - NPE fix itemstack.getItem()
            this.writeShort(-1);
        } else {
            this.writeShort(Item.getId(itemstack.getItem()));
            this.writeByte(itemstack.count);
            this.writeShort(itemstack.getData());
            NBTTagCompound nbttagcompound = null;

            if (itemstack.getItem().usesDurability() || itemstack.getItem().p()) {
                // Spigot start - filter
                itemstack = itemstack.cloneItemStack();
                CraftItemStack.setItemMeta(itemstack, CraftItemStack.getItemMeta(itemstack));
                // Spigot end
                nbttagcompound = itemstack.getTag();
            }

            this.a(nbttagcompound);
        }

    }

    public ItemStack i() throws IOException {
        ItemStack itemstack = null;
        short short0 = this.readShort();

        if (short0 >= 0) {
            byte b0 = this.readByte();
            short short1 = this.readShort();

            itemstack = new ItemStack(Item.getById(short0), b0, short1);
            itemstack.setTag(this.h());
            // CraftBukkit start
            if (itemstack.getTag() != null) {
                CraftItemStack.setItemMeta(itemstack, CraftItemStack.getItemMeta(itemstack));
            }
            // CraftBukkit end
        }

        return itemstack;
    }

    public String c(int i) {
        int j = this.e();

        if (j > i * 4) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + i * 4 + ")");
        } else if (j < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        } else {
            // PandaSpigot start - Switch from readBytes().array() to readBytes(byte[]) as we could be dealing with a DirectByteBuf
            byte[] b = new byte[j];
            this.readBytes(b);
            String s = new String(b, Charsets.UTF_8);
            // PandaSpigot end

            if (s.length() > i) {
                throw new DecoderException("The received string length is longer than maximum allowed (" + j + " > " + i + ")");
            } else {
                return s;
            }
        }
    }

    public PacketDataSerializer a(String s) {
        // PandaSpigot start - Optimize string writing
        int utf8Bytes = io.netty.buffer.ByteBufUtil.utf8Bytes(s);
        if (utf8Bytes > 32767) {
            throw new EncoderException("String too big (was " + s.length() + " bytes encoded, max " + 32767 + ")");
        } else {
            this.b(utf8Bytes);
            this.writeCharSequence(s, Charsets.UTF_8);
            return this;
        }
        // PandaSpigot end
    }

    public int capacity() {
        return this.a.capacity();
    }

    public ByteBuf capacity(int i) {
        return this.a.capacity(i);
    }

    public int maxCapacity() {
        return this.a.maxCapacity();
    }

    public ByteBufAllocator alloc() {
        return this.a.alloc();
    }

    public ByteOrder order() {
        return this.a.order();
    }

    public ByteBuf order(ByteOrder byteorder) {
        return this.a.order(byteorder);
    }

    public ByteBuf unwrap() {
        return this.a.unwrap();
    }

    public boolean isDirect() {
        return this.a.isDirect();
    }

    public int readerIndex() {
        return this.a.readerIndex();
    }

    public ByteBuf readerIndex(int i) {
        return this.a.readerIndex(i);
    }

    public int writerIndex() {
        return this.a.writerIndex();
    }

    public ByteBuf writerIndex(int i) {
        return this.a.writerIndex(i);
    }

    public ByteBuf setIndex(int i, int j) {
        return this.a.setIndex(i, j);
    }

    public int readableBytes() {
        return this.a.readableBytes();
    }

    public int writableBytes() {
        return this.a.writableBytes();
    }

    public int maxWritableBytes() {
        return this.a.maxWritableBytes();
    }

    public boolean isReadable() {
        return this.a.isReadable();
    }

    public boolean isReadable(int i) {
        return this.a.isReadable(i);
    }

    public boolean isWritable() {
        return this.a.isWritable();
    }

    public boolean isWritable(int i) {
        return this.a.isWritable(i);
    }

    public ByteBuf clear() {
        return this.a.clear();
    }

    public ByteBuf markReaderIndex() {
        return this.a.markReaderIndex();
    }

    public ByteBuf resetReaderIndex() {
        return this.a.resetReaderIndex();
    }

    public ByteBuf markWriterIndex() {
        return this.a.markWriterIndex();
    }

    public ByteBuf resetWriterIndex() {
        return this.a.resetWriterIndex();
    }

    public ByteBuf discardReadBytes() {
        return this.a.discardReadBytes();
    }

    public ByteBuf discardSomeReadBytes() {
        return this.a.discardSomeReadBytes();
    }

    public ByteBuf ensureWritable(int i) {
        return this.a.ensureWritable(i);
    }

    public int ensureWritable(int i, boolean flag) {
        return this.a.ensureWritable(i, flag);
    }

    public boolean getBoolean(int i) {
        return this.a.getBoolean(i);
    }

    public byte getByte(int i) {
        return this.a.getByte(i);
    }

    public short getUnsignedByte(int i) {
        return this.a.getUnsignedByte(i);
    }

    public short getShort(int i) {
        return this.a.getShort(i);
    }

    public int getUnsignedShort(int i) {
        return this.a.getUnsignedShort(i);
    }

    public int getMedium(int i) {
        return this.a.getMedium(i);
    }

    public int getUnsignedMedium(int i) {
        return this.a.getUnsignedMedium(i);
    }

    public int getInt(int i) {
        return this.a.getInt(i);
    }

    public long getUnsignedInt(int i) {
        return this.a.getUnsignedInt(i);
    }

    public long getLong(int i) {
        return this.a.getLong(i);
    }

    public char getChar(int i) {
        return this.a.getChar(i);
    }

    public float getFloat(int i) {
        return this.a.getFloat(i);
    }

    public double getDouble(int i) {
        return this.a.getDouble(i);
    }

    public ByteBuf getBytes(int i, ByteBuf bytebuf) {
        return this.a.getBytes(i, bytebuf);
    }

    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j) {
        return this.a.getBytes(i, bytebuf, j);
    }

    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
        return this.a.getBytes(i, bytebuf, j, k);
    }

    public ByteBuf getBytes(int i, byte[] abyte) {
        return this.a.getBytes(i, abyte);
    }

    public ByteBuf getBytes(int i, byte[] abyte, int j, int k) {
        return this.a.getBytes(i, abyte, j, k);
    }

    public ByteBuf getBytes(int i, ByteBuffer bytebuffer) {
        return this.a.getBytes(i, bytebuffer);
    }

    public ByteBuf getBytes(int i, OutputStream outputstream, int j) throws IOException {
        return this.a.getBytes(i, outputstream, j);
    }

    public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) throws IOException {
        return this.a.getBytes(i, gatheringbytechannel, j);
    }

    public ByteBuf setBoolean(int i, boolean flag) {
        return this.a.setBoolean(i, flag);
    }

    public ByteBuf setByte(int i, int j) {
        return this.a.setByte(i, j);
    }

    public ByteBuf setShort(int i, int j) {
        return this.a.setShort(i, j);
    }

    public ByteBuf setMedium(int i, int j) {
        return this.a.setMedium(i, j);
    }

    public ByteBuf setInt(int i, int j) {
        return this.a.setInt(i, j);
    }

    public ByteBuf setLong(int i, long j) {
        return this.a.setLong(i, j);
    }

    public ByteBuf setChar(int i, int j) {
        return this.a.setChar(i, j);
    }

    public ByteBuf setFloat(int i, float f) {
        return this.a.setFloat(i, f);
    }

    public ByteBuf setDouble(int i, double d0) {
        return this.a.setDouble(i, d0);
    }

    public ByteBuf setBytes(int i, ByteBuf bytebuf) {
        return this.a.setBytes(i, bytebuf);
    }

    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j) {
        return this.a.setBytes(i, bytebuf, j);
    }

    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
        return this.a.setBytes(i, bytebuf, j, k);
    }

    public ByteBuf setBytes(int i, byte[] abyte) {
        return this.a.setBytes(i, abyte);
    }

    public ByteBuf setBytes(int i, byte[] abyte, int j, int k) {
        return this.a.setBytes(i, abyte, j, k);
    }

    public ByteBuf setBytes(int i, ByteBuffer bytebuffer) {
        return this.a.setBytes(i, bytebuffer);
    }

    public int setBytes(int i, InputStream inputstream, int j) throws IOException {
        return this.a.setBytes(i, inputstream, j);
    }

    public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) throws IOException {
        return this.a.setBytes(i, scatteringbytechannel, j);
    }

    public ByteBuf setZero(int i, int j) {
        return this.a.setZero(i, j);
    }

    public boolean readBoolean() {
        return this.a.readBoolean();
    }

    public byte readByte() {
        return this.a.readByte();
    }

    public short readUnsignedByte() {
        return this.a.readUnsignedByte();
    }

    public short readShort() {
        return this.a.readShort();
    }

    public int readUnsignedShort() {
        return this.a.readUnsignedShort();
    }

    public int readMedium() {
        return this.a.readMedium();
    }

    public int readUnsignedMedium() {
        return this.a.readUnsignedMedium();
    }

    public int readInt() {
        return this.a.readInt();
    }

    public long readUnsignedInt() {
        return this.a.readUnsignedInt();
    }

    public long readLong() {
        return this.a.readLong();
    }

    public char readChar() {
        return this.a.readChar();
    }

    public float readFloat() {
        return this.a.readFloat();
    }

    public double readDouble() {
        return this.a.readDouble();
    }

    public ByteBuf readBytes(int i) {
        return this.a.readBytes(i);
    }

    public ByteBuf readSlice(int i) {
        return this.a.readSlice(i);
    }

    public ByteBuf readBytes(ByteBuf bytebuf) {
        return this.a.readBytes(bytebuf);
    }

    public ByteBuf readBytes(ByteBuf bytebuf, int i) {
        return this.a.readBytes(bytebuf, i);
    }

    public ByteBuf readBytes(ByteBuf bytebuf, int i, int j) {
        return this.a.readBytes(bytebuf, i, j);
    }

    public ByteBuf readBytes(byte[] abyte) {
        return this.a.readBytes(abyte);
    }

    public ByteBuf readBytes(byte[] abyte, int i, int j) {
        return this.a.readBytes(abyte, i, j);
    }

    public ByteBuf readBytes(ByteBuffer bytebuffer) {
        return this.a.readBytes(bytebuffer);
    }

    public ByteBuf readBytes(OutputStream outputstream, int i) throws IOException {
        return this.a.readBytes(outputstream, i);
    }

    public int readBytes(GatheringByteChannel gatheringbytechannel, int i) throws IOException {
        return this.a.readBytes(gatheringbytechannel, i);
    }

    public ByteBuf skipBytes(int i) {
        return this.a.skipBytes(i);
    }

    public ByteBuf writeBoolean(boolean flag) {
        return this.a.writeBoolean(flag);
    }

    public ByteBuf writeByte(int i) {
        return this.a.writeByte(i);
    }

    public ByteBuf writeShort(int i) {
        return this.a.writeShort(i);
    }

    public ByteBuf writeMedium(int i) {
        return this.a.writeMedium(i);
    }

    public ByteBuf writeInt(int i) {
        return this.a.writeInt(i);
    }

    public ByteBuf writeLong(long i) {
        return this.a.writeLong(i);
    }

    public ByteBuf writeChar(int i) {
        return this.a.writeChar(i);
    }

    public ByteBuf writeFloat(float f) {
        return this.a.writeFloat(f);
    }

    public ByteBuf writeDouble(double d0) {
        return this.a.writeDouble(d0);
    }

    public ByteBuf writeBytes(ByteBuf bytebuf) {
        return this.a.writeBytes(bytebuf);
    }

    public ByteBuf writeBytes(ByteBuf bytebuf, int i) {
        return this.a.writeBytes(bytebuf, i);
    }

    public ByteBuf writeBytes(ByteBuf bytebuf, int i, int j) {
        return this.a.writeBytes(bytebuf, i, j);
    }

    public ByteBuf writeBytes(byte[] abyte) {
        return this.a.writeBytes(abyte);
    }

    public ByteBuf writeBytes(byte[] abyte, int i, int j) {
        return this.a.writeBytes(abyte, i, j);
    }

    public ByteBuf writeBytes(ByteBuffer bytebuffer) {
        return this.a.writeBytes(bytebuffer);
    }

    public int writeBytes(InputStream inputstream, int i) throws IOException {
        return this.a.writeBytes(inputstream, i);
    }

    public int writeBytes(ScatteringByteChannel scatteringbytechannel, int i) throws IOException {
        return this.a.writeBytes(scatteringbytechannel, i);
    }

    public ByteBuf writeZero(int i) {
        return this.a.writeZero(i);
    }

    public int indexOf(int i, int j, byte b0) {
        return this.a.indexOf(i, j, b0);
    }

    public int bytesBefore(byte b0) {
        return this.a.bytesBefore(b0);
    }

    public int bytesBefore(int i, byte b0) {
        return this.a.bytesBefore(i, b0);
    }

    public int bytesBefore(int i, int j, byte b0) {
        return this.a.bytesBefore(i, j, b0);
    }

    public int forEachByte(ByteBufProcessor bytebufprocessor) {
        return this.a.forEachByte(bytebufprocessor);
    }

    public int forEachByte(int i, int j, ByteBufProcessor bytebufprocessor) {
        return this.a.forEachByte(i, j, bytebufprocessor);
    }

    public int forEachByteDesc(ByteBufProcessor bytebufprocessor) {
        return this.a.forEachByteDesc(bytebufprocessor);
    }

    public int forEachByteDesc(int i, int j, ByteBufProcessor bytebufprocessor) {
        return this.a.forEachByteDesc(i, j, bytebufprocessor);
    }

    public ByteBuf copy() {
        return this.a.copy();
    }

    public ByteBuf copy(int i, int j) {
        return this.a.copy(i, j);
    }

    public ByteBuf slice() {
        return this.a.slice();
    }

    public ByteBuf slice(int i, int j) {
        return this.a.slice(i, j);
    }

    public ByteBuf duplicate() {
        return this.a.duplicate();
    }

    public int nioBufferCount() {
        return this.a.nioBufferCount();
    }

    public ByteBuffer nioBuffer() {
        return this.a.nioBuffer();
    }

    public ByteBuffer nioBuffer(int i, int j) {
        return this.a.nioBuffer(i, j);
    }

    public ByteBuffer internalNioBuffer(int i, int j) {
        return this.a.internalNioBuffer(i, j);
    }

    public ByteBuffer[] nioBuffers() {
        return this.a.nioBuffers();
    }

    public ByteBuffer[] nioBuffers(int i, int j) {
        return this.a.nioBuffers(i, j);
    }

    public boolean hasArray() {
        return this.a.hasArray();
    }

    public byte[] array() {
        return this.a.array();
    }

    public int arrayOffset() {
        return this.a.arrayOffset();
    }

    public boolean hasMemoryAddress() {
        return this.a.hasMemoryAddress();
    }

    public long memoryAddress() {
        return this.a.memoryAddress();
    }

    public String toString(Charset charset) {
        return this.a.toString(charset);
    }

    public String toString(int i, int j, Charset charset) {
        return this.a.toString(i, j, charset);
    }

    public int hashCode() {
        return this.a.hashCode();
    }

    public boolean equals(Object object) {
        return this.a.equals(object);
    }

    public int compareTo(ByteBuf bytebuf) {
        return this.a.compareTo(bytebuf);
    }

    public String toString() {
        return this.a.toString();
    }

    public ByteBuf retain(int i) {
        return this.a.retain(i);
    }

    public ByteBuf retain() {
        return this.a.retain();
    }

    public int refCnt() {
        return this.a.refCnt();
    }

    public boolean release() {
        return this.a.release();
    }

    public boolean release(int i) {
        return this.a.release(i);
    }
    
    // PandaSpigot start - Delegate new Netty 4.1 methods.
    @Override
    public boolean isReadOnly() {
        return a.isReadOnly();
    }
    
    @Override
    public ByteBuf asReadOnly() {
        return a.asReadOnly();
    }
    
    @Override
    public int maxFastWritableBytes() {
        return a.maxFastWritableBytes();
    }
    
    @Override
    public short getShortLE(int index) {
        return a.getShortLE(index);
    }
    
    @Override
    public int getUnsignedShortLE(int index) {
        return a.getUnsignedShortLE(index);
    }
    
    @Override
    public int getMediumLE(int index) {
        return a.getMediumLE(index);
    }
    
    @Override
    public int getUnsignedMediumLE(int index) {
        return a.getUnsignedMediumLE(index);
    }
    
    @Override
    public int getIntLE(int index) {
        return a.getIntLE(index);
    }
    
    @Override
    public long getUnsignedIntLE(int index) {
        return a.getUnsignedIntLE(index);
    }
    
    @Override
    public long getLongLE(int index) {
        return a.getLongLE(index);
    }
    
    @Override
    public float getFloatLE(int index) {
        return a.getFloatLE(index);
    }
    
    @Override
    public double getDoubleLE(int index) {
        return a.getDoubleLE(index);
    }
    
    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return a.getBytes(index, out, position, length);
    }
    
    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return a.getCharSequence(index, length, charset);
    }
    
    @Override
    public ByteBuf setShortLE(int index, int value) {
        return a.setShortLE(index, value);
    }
    
    @Override
    public ByteBuf setMediumLE(int index, int value) {
        return a.setMediumLE(index, value);
    }
    
    @Override
    public ByteBuf setIntLE(int index, int value) {
        return a.setIntLE(index, value);
    }
    
    @Override
    public ByteBuf setLongLE(int index, long value) {
        return a.setLongLE(index, value);
    }
    
    @Override
    public ByteBuf setFloatLE(int index, float value) {
        return a.setFloatLE(index, value);
    }
    
    @Override
    public ByteBuf setDoubleLE(int index, double value) {
        return a.setDoubleLE(index, value);
    }
    
    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return a.setBytes(index, in, position, length);
    }
    
    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return a.setCharSequence(index, sequence, charset);
    }
    
    @Override
    public short readShortLE() {
        return a.readShortLE();
    }
    
    @Override
    public int readUnsignedShortLE() {
        return a.readUnsignedShortLE();
    }
    
    @Override
    public int readMediumLE() {
        return a.readMediumLE();
    }
    
    @Override
    public int readUnsignedMediumLE() {
        return a.readUnsignedMediumLE();
    }
    
    @Override
    public int readIntLE() {
        return a.readIntLE();
    }
    
    @Override
    public long readUnsignedIntLE() {
        return a.readUnsignedIntLE();
    }
    
    @Override
    public long readLongLE() {
        return a.readLongLE();
    }
    
    @Override
    public float readFloatLE() {
        return a.readFloatLE();
    }
    
    @Override
    public double readDoubleLE() {
        return a.readDoubleLE();
    }
    
    @Override
    public ByteBuf readRetainedSlice(int length) {
        return a.readRetainedSlice(length);
    }
    
    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return a.readCharSequence(length, charset);
    }
    
    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        return a.readBytes(out, position, length);
    }
    
    @Override
    public ByteBuf writeShortLE(int value) {
        return a.writeShortLE(value);
    }
    
    @Override
    public ByteBuf writeMediumLE(int value) {
        return a.writeMediumLE(value);
    }
    
    @Override
    public ByteBuf writeIntLE(int value) {
        return a.writeIntLE(value);
    }
    
    @Override
    public ByteBuf writeLongLE(long value) {
        return a.writeLongLE(value);
    }
    
    @Override
    public ByteBuf writeFloatLE(float value) {
        return a.writeFloatLE(value);
    }
    
    @Override
    public ByteBuf writeDoubleLE(double value) {
        return a.writeDoubleLE(value);
    }
    
    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        return a.writeBytes(in, position, length);
    }
    
    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        return a.writeCharSequence(sequence, charset);
    }
    
    @Override
    public int forEachByte(ByteProcessor processor) {
        return a.forEachByte(processor);
    }
    
    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        return a.forEachByte(index, length, processor);
    }
    
    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        return a.forEachByteDesc(processor);
    }
    
    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return a.forEachByteDesc(index, length, processor);
    }
    
    @Override
    public ByteBuf retainedSlice() {
        return a.retainedSlice();
    }
    
    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return a.retainedSlice(index, length);
    }
    
    @Override
    public ByteBuf retainedDuplicate() {
        return a.retainedDuplicate();
    }
    
    @Override
    public boolean isContiguous() {
        return a.isContiguous();
    }
    
    @Override
    public ByteBuf touch() {
        return a.touch();
    }
    
    @Override
    public ByteBuf touch(Object hint) {
        return a.touch(hint);
    }
    // PandaSpigot end
}
