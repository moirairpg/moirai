package es.thalesalv.chatrpg.adapters.data.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "channel")
public class Channel {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "channel_id", unique = true, nullable = false)
    private String channelId;

    @OneToOne
    @JoinColumn(name = "channel_config_id", referencedColumnName = "id", nullable = false)
    private ChannelConfig channelConfig;
}
