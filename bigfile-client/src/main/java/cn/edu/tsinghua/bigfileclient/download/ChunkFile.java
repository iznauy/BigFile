package cn.edu.tsinghua.bigfileclient.download;

import cn.edu.tsinghua.bigfileclient.tools.FileReader;
import okio.Buffer;

import java.io.*;

/**
 * Created on 2020-09-29.
 * Description:
 *
 * @author iznauy
 */
public class ChunkFile {

    private static final String ChunkFileNameTemplate = "%s.%d.temp";

    private File chunkFile;

    public ChunkFile(File baseDir, String fileId, long chunkId) {
        this.chunkFile = new File(baseDir, String.format(ChunkFileNameTemplate, fileId, chunkId));
    }

    public void clear() throws IOException {
        if (!chunkFile.exists()) {
            return;
        }
        FileWriter fileWriter = new FileWriter(chunkFile);
        fileWriter.write("");
        fileWriter.flush();
        fileWriter.close();
    }

    public boolean exists() {
        return chunkFile.exists();
    }


    public byte[] read() throws IOException {
        return FileReader.read(chunkFile, 0, chunkFile.length());
    }

    public void append(byte[] data) throws IOException {
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(chunkFile, true))) {
            out.write(data);
            out.flush();
        }
    }

    public long length() {
        if (chunkFile.exists()) {
            return chunkFile.length();
        }
        return 0;
    }

}
