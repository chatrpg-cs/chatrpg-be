package es.thalesalv.chatrpg.adapters.data.entity;

import java.util.Map;
import java.util.Set;

import org.hibernate.annotations.GenericGenerator;

import es.thalesalv.chatrpg.application.util.dbutils.StringMapDoubleConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "moderation_settings")
public class ModerationSettingsEntity {

    @Id
    @GeneratedValue(generator = "nanoid-generator")
    @GenericGenerator(name = "nanoid-generator", strategy = "es.thalesalv.chatrpg.application.util.dbutils.NanoIdIdentifierGenerator")
    private String id;

    @Column(name = "owner_discord_id", nullable = false)
    private String owner;

    @Column(name = "is_absolute_moderation", nullable = false, columnDefinition = "boolean default false")
    private boolean isAbsolute;

    @Column(name = "thresholds", nullable = true)
    @Convert(converter = StringMapDoubleConverter.class)
    private Map<String, Double> thresholds;

    @OneToMany(mappedBy = "moderationSettings")
    private Set<ChannelConfigEntity> channelConfigs;
}
