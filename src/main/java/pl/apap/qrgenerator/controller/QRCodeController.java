package pl.apap.qrgenerator.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.apap.qrgenerator.Data.QRCode;
import pl.apap.qrgenerator.Data.User;
import pl.apap.qrgenerator.repository.QRCodeRepository;
import pl.apap.qrgenerator.repository.UserRepository;
import pl.apap.qrgenerator.service.QRCodeService;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class QRCodeController {

    private static final Logger logger = LoggerFactory.getLogger(QRCodeController.class);

    private final QRCodeService qrCodeService;
    private final QRCodeRepository qrCodeRepository;
    private final UserRepository userRepository;

    public QRCodeController(QRCodeService qrCodeService, QRCodeRepository qrCodeRepository, UserRepository userRepository) {
        this.qrCodeService = qrCodeService;
        this.qrCodeRepository = qrCodeRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user != null) {
            model.addAttribute("qrCodes", qrCodeRepository.findByUser(user));
        } else {
            model.addAttribute("qrCodes", null);
            model.addAttribute("error", "User not found");
        }

        return "dashboard";
    }

    @PostMapping("/generate")
    public String generateQRCode(@RequestParam("url") String url, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);

            if (user != null) {
                String directoryPath = System.getProperty("user.dir") + "/qrCodes/";
                File directory = new File(directoryPath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String filePath = directoryPath + "qr_" + user.getUsername() + "_" + System.currentTimeMillis() + ".png";

                qrCodeService.generateQRCodeImage(url, 350, 350, filePath);

                String relativePath = "/qrCodes/" + new File(filePath).getName();

                QRCode qrCode = new QRCode();
                qrCode.setUrl(url);
                qrCode.setQrCodeImagePatch(relativePath);
                qrCode.setUser(user);
                qrCodeRepository.save(qrCode);

                System.out.println("Generated QR code path: " + qrCode.getQrCodeImagePatch());

                return "redirect:/dashboard";
            } else {
                return "redirect:/error?message=User+not+found";
            }
        } catch (Exception e) {
            logger.error("Error generating QR code", e);
            return "redirect:/error?message=Error+generating+QR+code";
        }
    }


    private String generateFilePath(String username) {
        String directoryPath = System.getProperty("user.dir") + "/src/main/resources/static/qrCodes/";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directoryPath + "qr_" + username + "_" + System.currentTimeMillis() + ".png";
    }
}
