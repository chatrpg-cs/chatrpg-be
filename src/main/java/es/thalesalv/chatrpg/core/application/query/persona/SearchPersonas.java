package es.thalesalv.chatrpg.core.application.query.persona;

import es.thalesalv.chatrpg.common.cqrs.query.Query;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(builderClassName = "Builder")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SearchPersonas extends Query<SearchPersonasResult> {

    private final Integer page;
    private final Integer results;
    private final String searchField;
    private final String searchCriteria;
    private final String sortByField;
    private final String order;
}
