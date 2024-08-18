package pl.apap.qrgenerator.Data;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class QRCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    private String filePath;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
