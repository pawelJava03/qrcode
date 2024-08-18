package pl.apap.qrgenerator.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;

@Service
public class QRCodeService {

    public String generateQRCodeImage(String text, int width, int height, String filepath) throws Exception {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);
        Path path = FileSystems.getDefault().getPath(filepath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        return filepath;
    }

    public void generateQRCodeImage(String text, int width, int height, OutputStream outputStream) throws Exception {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
    }
}
