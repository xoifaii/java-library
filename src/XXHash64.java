
// Based on my own implementation of XXHash32 in luau but updated to use java's long type -> https://github.com/daily3014/rbx-cryptography/blob/main/src/Hashing/XXH32.luau
import static java.lang.Long.rotateLeft;

public class XXHash64 {
  private static final long PRIME64_1 = -7046029288634856825L;
  private static final long PRIME64_2 = -4417276706812531889L;
  private static final long PRIME64_3 = 1609587929392839161L;
  private static final long PRIME64_4 = -8796714831421723037L;
  private static final long PRIME64_5 = 2870177450012600261L;

  public static long hash64(byte[] buf, int off, int len, long seed) {
    int end = off + len;
    long h64;
    if (len >= 32) {
      int limit = end - 32;
      long v1 = seed + PRIME64_1 + PRIME64_2;
      long v2 = seed + PRIME64_2;
      long v3 = seed + 0;
      long v4 = seed - PRIME64_1;
      do {
        v1 += readLongLE(buf, off) * PRIME64_2;
        v1 = rotateLeft(v1, 31);
        v1 *= PRIME64_1;
        off += 8;
        v2 += readLongLE(buf, off) * PRIME64_2;
        v2 = rotateLeft(v2, 31);
        v2 *= PRIME64_1;
        off += 8;
        v3 += readLongLE(buf, off) * PRIME64_2;
        v3 = rotateLeft(v3, 31);
        v3 *= PRIME64_1;
        off += 8;
        v4 += readLongLE(buf, off) * PRIME64_2;
        v4 = rotateLeft(v4, 31);
        v4 *= PRIME64_1;
        off += 8;
      } while (off <= limit);
      h64 = rotateLeft(v1, 1) + rotateLeft(v2, 7) + rotateLeft(v3, 12) + rotateLeft(v4, 18);
      v1 *= PRIME64_2;
      v1 = rotateLeft(v1, 31);
      v1 *= PRIME64_1;
      h64 ^= v1;
      h64 = h64 * PRIME64_1 + PRIME64_4;
      v2 *= PRIME64_2;
      v2 = rotateLeft(v2, 31);
      v2 *= PRIME64_1;
      h64 ^= v2;
      h64 = h64 * PRIME64_1 + PRIME64_4;
      v3 *= PRIME64_2;
      v3 = rotateLeft(v3, 31);
      v3 *= PRIME64_1;
      h64 ^= v3;
      h64 = h64 * PRIME64_1 + PRIME64_4;
      v4 *= PRIME64_2;
      v4 = rotateLeft(v4, 31);
      v4 *= PRIME64_1;
      h64 ^= v4;
      h64 = h64 * PRIME64_1 + PRIME64_4;
    } else {
      h64 = seed + PRIME64_5;
    }
    h64 += len;
    while (off <= end - 8) {
      long k1 = readLongLE(buf, off);
      k1 *= PRIME64_2;
      k1 = rotateLeft(k1, 31);
      k1 *= PRIME64_1;
      h64 ^= k1;
      h64 = rotateLeft(h64, 27) * PRIME64_1 + PRIME64_4;
      off += 8;
    }
    if (off <= end - 4) {
      h64 ^= (readIntLE(buf, off) & 0xFFFFFFFFL) * PRIME64_1;
      h64 = rotateLeft(h64, 23) * PRIME64_2 + PRIME64_3;
      off += 4;
    }
    while (off < end) {
      h64 ^= (readByte(buf, off) & 0xFF) * PRIME64_5;
      h64 = rotateLeft(h64, 11) * PRIME64_1;
      ++off;
    }
    h64 ^= h64 >>> 33;
    h64 *= PRIME64_2;
    h64 ^= h64 >>> 29;
    h64 *= PRIME64_3;
    h64 ^= h64 >>> 32;
    return h64;
  }

  private static byte readByte(byte[] buf, int i) {
    return buf[i];
  }

  private static int readIntLE(byte[] buf, int i) {
    return (buf[i] & 0xFF) | ((buf[i + 1] & 0xFF) << 8) | ((buf[i + 2] & 0xFF) << 16)
        | ((buf[i + 3] & 0xFF) << 24);
  }

  private static long readLongLE(byte[] buf, int i) {
    return (buf[i] & 0xFFL) | ((buf[i + 1] & 0xFFL) << 8) | ((buf[i + 2] & 0xFFL) << 16)
        | ((buf[i + 3] & 0xFFL) << 24) | ((buf[i + 4] & 0xFFL) << 32)
        | ((buf[i + 5] & 0xFFL) << 40) | ((buf[i + 6] & 0xFFL) << 48)
        | ((buf[i + 7] & 0xFFL) << 56);
  }
}