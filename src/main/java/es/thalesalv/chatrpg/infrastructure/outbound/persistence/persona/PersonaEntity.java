package es.thalesalv.chatrpg.infrastructure.outbound.persistence.persona;

import java.time.OffsetDateTime;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import es.thalesalv.chatrpg.common.dbutil.NanoIdIdentifierGenerator;
import es.thalesalv.chatrpg.infrastructure.outbound.persistence.ShareableAssetEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "Persona")
@Table(name = "persona")
public class PersonaEntity extends ShareableAssetEntity {

    @Id
    @GeneratedValue(generator = "nanoid-generator")
    @GenericGenerator(name = "nanoid-generator", type = NanoIdIdentifierGenerator.class)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "personality", nullable = false)
    private String personality;

    @Embedded
    private NudgeEntity nudge;

    @Embedded
    private BumpEntity bump;

    @Column(name = "game_mode", nullable = false)
    private String gameMode;

    private PersonaEntity(Builder builder) {

        super(builder.creatorDiscordId, builder.creationDate,
                builder.lastUpdateDate, builder.ownerDiscordId, builder.usersAllowedToRead, builder.usersAllowedToWrite,
                builder.visibility);

        this.id = builder.id;
        this.name = builder.name;
        this.personality = builder.personality;
        this.nudge = builder.nudge;
        this.bump = builder.bump;
        this.gameMode = builder.gameMode;
    }

    protected PersonaEntity() {
        super();
    }

    public static Builder builder() {

        return new Builder();
    }

    public static class Builder {

        private String id;
        private String name;
        private String personality;
        private NudgeEntity nudge;
        private BumpEntity bump;
        private String ownerDiscordId;
        private List<String> usersAllowedToRead;
        private List<String> usersAllowedToWrite;
        private String visibility;
        private String gameMode;
        private String creatorDiscordId;
        private OffsetDateTime creationDate;
        private OffsetDateTime lastUpdateDate;

        public Builder id(String id) {

            this.id = id;
            return this;
        }

        public Builder name(String name) {

            this.name = name;
            return this;
        }

        public Builder personality(String personality) {

            this.personality = personality;
            return this;
        }

        public Builder nudge(NudgeEntity nudge) {

            this.nudge = nudge;
            return this;
        }

        public Builder bump(BumpEntity bump) {

            this.bump = bump;
            return this;
        }

        public Builder visibility(String visibility) {

            this.visibility = visibility;
            return this;
        }

        public Builder ownerDiscordId(String ownerDiscordId) {

            this.ownerDiscordId = ownerDiscordId;
            return this;
        }

        public Builder usersAllowedToRead(List<String> usersAllowedToRead) {

            this.usersAllowedToRead = usersAllowedToRead;
            return this;
        }

        public Builder usersAllowedToWrite(List<String> usersAllowedToWrite) {

            this.usersAllowedToWrite = usersAllowedToWrite;
            return this;
        }

        public Builder gameMode(String gameMode) {

            this.gameMode = gameMode;
            return this;
        }

        public Builder creatorDiscordId(String creatorDiscordId) {

            this.creatorDiscordId = creatorDiscordId;
            return this;
        }

        public Builder creationDate(OffsetDateTime creationDate) {

            this.creationDate = creationDate;
            return this;
        }

        public Builder lastUpdateDate(OffsetDateTime lastUpdateDate) {

            this.lastUpdateDate = lastUpdateDate;
            return this;
        }

        public PersonaEntity build() {

            return new PersonaEntity(this);
        }
    }
}
