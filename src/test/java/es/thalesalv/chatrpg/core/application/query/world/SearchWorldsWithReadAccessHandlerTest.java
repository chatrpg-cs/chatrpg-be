package es.thalesalv.chatrpg.core.application.query.world;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.application.usecase.world.SearchWorldsWithReadAccessHandler;
import es.thalesalv.chatrpg.core.application.usecase.world.request.SearchWorldsWithReadAccess;
import es.thalesalv.chatrpg.core.application.usecase.world.result.SearchWorldsResult;
import es.thalesalv.chatrpg.core.domain.world.WorldRepository;

@ExtendWith(MockitoExtension.class)
public class SearchWorldsWithReadAccessHandlerTest {

    @Mock
    private WorldRepository repository;

    @InjectMocks
    private SearchWorldsWithReadAccessHandler handler;

    @Test
    public void searchWorlds() {

        // Given
        SearchWorldsWithReadAccess query = SearchWorldsWithReadAccess.builder()
                .direction("ASC")
                .page(1)
                .items(2)
                .sortByField("name")
                .build();

        SearchWorldsResult expectedResult = SearchWorldsResult.builder()
                .page(1)
                .items(2)
                .build();

        when(repository.searchWorldsWithReadAccess(any(SearchWorldsWithReadAccess.class)))
                .thenReturn(expectedResult);

        // When
        SearchWorldsResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEqualTo(expectedResult.getItems());
        assertThat(result.getPage()).isEqualTo(expectedResult.getPage());
    }
}
