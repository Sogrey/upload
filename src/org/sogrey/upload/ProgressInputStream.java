package org.sogrey.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.sogrey.upload.FTP.UploadProgressListener;


public class ProgressInputStream extends InputStream {

    private static final int TEN_KILOBYTES = 1024 * 10;  //每上�?0K返回�?��

    private InputStream inputStream;

    private long progress;
    private long lastUpdate;

    private boolean closed;
    
    private UploadProgressListener listener;
    /**本地文件*/
    private File localFile;
    /**上传后文件名*/
    private String fileName;

    public ProgressInputStream(InputStream inputStream,UploadProgressListener listener,File localFile) {
        this.inputStream = inputStream;
        this.progress = 0;
        this.lastUpdate = 0;
        this.listener = listener;
        this.localFile = localFile;
        this.fileName = this.localFile.getPath().split("/")[this.localFile.getPath().split("/").length-1];
        
        this.closed = false;
    }

    @Override
    public int read() throws IOException {
        int count = inputStream.read();
        return incrementCounterAndUpdateDisplay(count);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count = inputStream.read(b, off, len);
        return incrementCounterAndUpdateDisplay(count);
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (closed)
            throw new IOException("already closed");
        closed = true;
    }

    private int incrementCounterAndUpdateDisplay(int count) {
        if (count > 0)
            progress += count;
        lastUpdate = maybeUpdateDisplay(progress, lastUpdate);
        return count;
    }

    private long maybeUpdateDisplay(long progress, long lastUpdate) {
        if (progress - lastUpdate > TEN_KILOBYTES) {
            lastUpdate = progress;
            this.listener.onUploadProgress(FTP.FTP_UPLOAD_LOADING, progress, this.localFile);
        }
        return lastUpdate;
    }
    
  
    
}
