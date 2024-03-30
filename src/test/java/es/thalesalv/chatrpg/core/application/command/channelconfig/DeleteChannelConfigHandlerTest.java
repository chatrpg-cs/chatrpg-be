package es.thalesalv.chatrpg.core.application.command.channelconfig;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigDomainService;

@ExtendWith(MockitoExtension.class)
public class DeleteChannelConfigHandlerTest {

    @Mock
    private ChannelConfigDomainService domainService;

    @InjectMocks
    private DeleteChannelConfigHandler handler;

    @Test
    public void errorWhenIdIsNull() {

        // Given
        String id = null;
        String requesterId = "RQSTRID";
        DeleteChannelConfig config = DeleteChannelConfig.build(id, requesterId);

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(config));
    }

    @Test
    public void deleteChannelConfig() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";

        DeleteChannelConfig command = DeleteChannelConfig.build(id, requesterId);

        doNothing().when(domainService).deleteChannelConfig(command);

        // Then
        handler.handle(command);
    }
}
