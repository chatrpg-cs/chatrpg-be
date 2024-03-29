package es.thalesalv.chatrpg.domain.model;

import es.thalesalv.chatrpg.domain.model.bot.Channel;
import es.thalesalv.chatrpg.domain.model.bot.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.openai.moderation.ModerationResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EventData {

    private Guild guild;
    private SelfUser bot;
    private Message message;
    private User messageAuthor;
    private SelfUser discordUser;
    private LorebookEntry lorebookEntry;
    private Message messageToBeEdited;
    private Channel channelDefinitions;
    private MessageChannelUnion currentChannel;
    private ModerationResult inputModerationResult;
    private ModerationResult outputModerationResult;
}
