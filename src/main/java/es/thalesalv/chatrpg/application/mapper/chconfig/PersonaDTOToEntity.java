package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.BumpEntity;
import es.thalesalv.chatrpg.adapters.data.entity.NudgeEntity;
import es.thalesalv.chatrpg.adapters.data.entity.PersonaEntity;
import es.thalesalv.chatrpg.domain.model.chconf.Bump;
import es.thalesalv.chatrpg.domain.model.chconf.Nudge;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;

@Component
public class PersonaDTOToEntity implements Function<Persona, PersonaEntity> {

    @Override
    public PersonaEntity apply(Persona t) {

        return PersonaEntity.builder()
                .id(t.getId())
                .name(t.getName())
                .owner(t.getOwner())
                .intent(t.getIntent())
                .personality(t.getPersonality())
                .bump(buildBump(t.getBump()))
                .nudge(buildNudge(t.getNudge()))
                .build();
    }

    private NudgeEntity buildNudge(Nudge nudge) {

        return Optional.ofNullable(nudge)
                .map(n -> NudgeEntity.builder()
                        .role(n.getRole())
                        .content(n.getContent())
                        .build())
                .orElse(NudgeEntity.builder()
                        .role(StringUtils.EMPTY)
                        .content(StringUtils.EMPTY)
                        .build());
    }

    private BumpEntity buildBump(Bump bump) {

        return Optional.ofNullable(bump)
                .map(b -> BumpEntity.builder()
                        .role(b.getRole())
                        .content(b.getContent())
                        .frequency(b.getFrequency())
                        .build())
                .orElse(BumpEntity.builder()
                        .content(StringUtils.EMPTY)
                        .role(StringUtils.EMPTY)
                        .frequency(0)
                        .build());
    }
}