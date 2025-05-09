package karol.przesylki.konduktorskie_przesylki.tables;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;

@Entity
@Data
public class Postbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="postbox_id")
    private Integer id;

    private String train_number;

    private String inside_item;

    private Double inside_item_price;
    
    private Double shipping_price;

    private Box_status status;

    private LocalDateTime transportDate;

    private Box_Dimensions type;

    @OneToOne
    private Post_Client src_client;

    @OneToOne
    private Post_Client dest_client;
}
