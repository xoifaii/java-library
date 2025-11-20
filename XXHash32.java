// Based on my own module in Luau -> https://github.com/daily3014/rbx-cryptography/blob/main/src/Hashing/XXH32.luau
public class XXHash32 {
  private static final int PRIME1 = (int) 2654435761L;
  private static final int PRIME2 = (int) 2246822519L;
  private static final int PRIME3 = (int) 3266489917L;
  private static final int PRIME4 = 668265263;
  private static final int PRIME5 = 374761393;

  public static int hash(byte[] data, int seed) {
    int hash;
    int index = 0;
    int length = data.length;

    if (length >= 16) {
      int limit = length - 16;
      int v1 = seed + PRIME1 + PRIME2;
      int v2 = seed + PRIME2;
      int v3 = seed;
      int v4 = seed - PRIME1;

      while (index <= limit) {
        v1 = Integer.rotateLeft(v1 + getInt(data, index) * PRIME2, 13) * PRIME1;
        index += 4;
        v2 = Integer.rotateLeft(v2 + getInt(data, index) * PRIME2, 13) * PRIME1;
        index += 4;
        v3 = Integer.rotateLeft(v3 + getInt(data, index) * PRIME2, 13) * PRIME1;
        index += 4;
        v4 = Integer.rotateLeft(v4 + getInt(data, index) * PRIME2, 13) * PRIME1;
        index += 4;
      }

      hash = Integer.rotateLeft(v1, 1) + Integer.rotateLeft(v2, 7) + Integer.rotateLeft(v3, 12)
          + Integer.rotateLeft(v4, 18);
    } else {
      hash = seed + PRIME5;
    }

    hash = hash + length;

    while (index <= length - 4) {
      hash = Integer.rotateLeft(hash + getInt(data, index) * PRIME3, 17) * PRIME4;
      index += 4;
    }

    while (index < length) {
      hash = Integer.rotateLeft(hash + (data[index] & 0xff) * PRIME5, 11) * PRIME1;
      index += 1;
    }

    hash = hash ^ (hash >>> 15);
    hash = hash * PRIME2;
    hash = hash ^ (hash >>> 13);
    hash = hash * PRIME3;
    hash = hash ^ (hash >>> 16);

    return hash;
  }

  private static int getInt(byte[] buffer, int index) {
    return (buffer[index] & 0xff) | ((buffer[index + 1] & 0xff) << 8)
        | ((buffer[index + 2] & 0xff) << 16) | ((buffer[index + 3] & 0xff) << 24);
  }
}