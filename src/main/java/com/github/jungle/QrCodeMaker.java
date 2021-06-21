package com.github.jungle;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

/**
 * 二维码构造工具。
 *
 * @author Darwin
 */
public class QrCodeMaker {

    /**
     * 二维码内容。
     */
    private String content;
    /**
     * 宽度。
     */
    private Integer width = 150;
    /**
     * 长度。
     */
    private Integer height = 150;

    private QrCodeMaker() {

    }

    public static QrCodeMaker getInstance() {
        return new QrCodeMaker();
    }


    /**
     * 设置二维码大小。
     *
     * @param width  宽度
     * @param height 高度
     * @return {@link QrCodeMaker}
     */
    public QrCodeMaker withSize(Integer width, Integer height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * 设置内容。
     *
     * @param content 内容
     * @return {@link QrCodeMaker}
     */
    public QrCodeMaker withContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * 将二维码内容以jpg编码并输出到给定的输出流。
     *
     * @param os 输出流
     * @throws WriterException 编码写入异常时抛出
     * @throws IOException     二维码图像IO写入异常时抛出
     */
    public void drainTo(OutputStream os) throws WriterException, IOException {
        final BufferedImage bufferedImage = createQrCode(content);
        ImageIO.write(bufferedImage, "jpg", os);
        os.flush();
        os.close();
    }

    /**
     * @return 二维码jpg以base64编码后的数据
     * @throws WriterException 编码写入异常时抛出
     * @throws IOException     二维码图像IO写入异常时抛出
     */
    public String toBase64() throws WriterException, IOException {
        final BufferedImage bufferedImage = createQrCode(content);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(baos.toByteArray());
    }

    /**
     * 根据所给内容创建二维码。
     *
     * @param content 信息内容
     * @return {@link BufferedImage}
     * @throws WriterException 写入异常
     */
    public BufferedImage createQrCode(String content) throws WriterException {
        final QRCodeWriter qrCodeWriter = new QRCodeWriter();
        final BitMatrix bitMatrix =
                qrCodeWriter.encode(content,
                        BarcodeFormat.QR_CODE, width, height);
        return createBufferedImageFromBitMatrix(bitMatrix);
    }

    /**
     * 将二值位图矩阵转为黑白RGB。
     *
     * @param bitMatrix 二值位图矩阵
     * @return {@link BufferedImage}
     */
    private BufferedImage createBufferedImageFromBitMatrix(BitMatrix bitMatrix) {

        /*获取图像矩阵宽度和高度*/
        final int width = bitMatrix.getWidth();
        final int height = bitMatrix.getHeight();
        int[] pixels = new int[width * height];

        final int black = new Color(0, 0, 0).getRGB();
        final int white = new Color(255, 255, 255).getRGB();
        /*按宽高以及二值像素填充矩阵*/
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final boolean b = bitMatrix.get(x, y);
                pixels[x * width + y] = b ? black : white;
            }
        }
        /*输出为图像*/
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        final WritableRaster raster = image.getRaster();
        raster.setDataElements(0, 0, width, height, pixels);
        return image;
    }
}
