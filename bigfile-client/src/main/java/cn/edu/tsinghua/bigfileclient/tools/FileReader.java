package cn.edu.tsinghua.bigfileclient.tools;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * Created on 2020-09-28.
 * Description:
 *
 * @author iznauy
 */
public class FileReader {

    public static byte[] read(File file, long offset, long span) throws IOException {
        RandomAccessFile bigFile = new RandomAccessFile(file, "r");
        bigFile.seek(offset);
        FileChannel channel = bigFile.getChannel();
        byte[] chunk = new byte[(int)span];
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int len, index = 0;
        while (index < span && (len = channel.read(buffer)) != -1) {
            buffer.flip();
            int size = len;
            if (size > span - index) {
                size = (int)(span - index);
            }
            buffer.get(chunk, index, size);
            index += size;
            buffer.clear();
        }
        if (index >= span) {
            return chunk;
        }
        return Arrays.copyOf(chunk, index);
    }

}
