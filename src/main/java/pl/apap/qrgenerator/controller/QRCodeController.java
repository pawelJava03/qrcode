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

@Controller
public class QRCodeController {

    private final QRCodeService qrCodeService;
    private final QRCodeRepository qrCodeRepository;
    private final UserRepository userRepository;

    // Wstrzykiwanie zależności przez konstruktor
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
            // Obsługuje przypadek, gdy użytkownik nie został znaleziony
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
                String filePath = "qr_" + user.getUsername() + "_" + System.currentTimeMillis() + ".png";
                qrCodeService.generateQRCodeImage(url, 350, 350, filePath);

                QRCode qrCode = new QRCode();
                qrCode.setUrl(url);
                qrCode.setQrCodeImagePatch(filePath);
                qrCode.setUser(user);
                qrCodeRepository.save(qrCode);

                return "redirect:/dashboard";
            } else {
                // Obsługuje przypadek, gdy użytkownik nie został znaleziony
                return "redirect:/error?message=User+not+found";
            }
        } catch (Exception e) {
            // Obsługuje błędy związane z generowaniem kodu QR
            return "redirect:/error?message=Error+generating+QR+code";
        }
    }
}
