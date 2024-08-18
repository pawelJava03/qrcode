package pl.apap.qrgenerator.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import pl.apap.qrgenerator.Data.QRCode;
import pl.apap.qrgenerator.Data.User;
import pl.apap.qrgenerator.repository.QRCodeRepository;
import pl.apap.qrgenerator.repository.UserRepository;
import pl.apap.qrgenerator.service.QRCodeService;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class QRCodeController {

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

                String fileName = "qr_" + user.getUsername() + "_" + System.currentTimeMillis() + ".png";
                String filePath = directoryPath + fileName;

                qrCodeService.generateQRCodeImage(url, 350, 350, filePath);

                QRCode qrCode = new QRCode();
                qrCode.setUrl(url);
                qrCode.setFilePath(filePath);  // Zapisujemy ścieżkę do pliku
                qrCode.setUser(user);
                qrCodeRepository.save(qrCode);

                return "redirect:/dashboard";
            } else {
                return "redirect:/error?message=User+not+found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error?message=Error+generating+QR+code";
        }
    }

    @GetMapping("/qrCodes/{fileName}")
    public ResponseEntity<Resource> getQRCodeImage(@PathVariable String fileName) {
        try {
            Path file = Paths.get(System.getProperty("user.dir") + "/qrCodes/").resolve(fileName);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(resource);
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
