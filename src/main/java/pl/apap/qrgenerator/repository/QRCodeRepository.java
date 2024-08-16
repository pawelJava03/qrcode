package pl.apap.qrgenerator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.apap.qrgenerator.Data.QRCode;
import pl.apap.qrgenerator.Data.User;

import java.util.List;

public interface QRCodeRepository extends JpaRepository<QRCode, Long> {
    List<QRCode> findByUser(User user);
}
