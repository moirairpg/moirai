package es.thalesalv.chatrpg.adapters.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class ChannelEntity {

    @Id
    @Column(name = "discord_channel_id", nullable = false, unique = true)
    private String id;

    @ManyToOne
    @JoinColumn(name = "channel_config_id", referencedColumnName = "id", nullable = false)
    private ChannelConfigEntity channelConfig;
}
