package kuro9.mahjongbot.instruction;

import kuro9.mahjongbot.DBScoreProcess;
import kuro9.mahjongbot.Logger;
import kuro9.mahjongbot.ResourceHandler;
import kuro9.mahjongbot.data.UserGameData;
import kuro9.mahjongbot.data.UserGameDataComparatorKt;
import kuro9.mahjongbot.exception.DBConnectException;
import kuro9.mahjongbot.instruction.action.RankInterface;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 전체 범위의 데이터 순위를 출력합니다.
 */
public class EntireRank extends RankArranger implements RankInterface {
    static int[] uma_page_count = {1};
    static Button[] uma_button = {
            Button.secondary("rank_uma_go_first", "<<"),
            Button.secondary("rank_uma_go_back", "<"),
            Button.primary("rank_uma_refresh", "F5"),
            Button.secondary("rank_uma_go_next", ">"),
            Button.secondary("rank_uma_go_last", ">>")
    };
    static int[] total_game_page_count = {1};
    static Button[] total_game_button = {
            Button.secondary("rank_totalgame_go_first", "<<"),
            Button.secondary("rank_totalgame_go_back", "<"),
            Button.primary("rank_totalgame_refresh", "F5"),
            Button.secondary("rank_totalgame_go_next", ">"),
            Button.secondary("rank_totalgame_go_last", ">>")
    };

    @Override
    public void summaryReply(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        ResourceBundle resourceBundle = ResourceHandler.getResource(event);

        int filter = getValidFilter(event);
        long guildId = getGuildID(event);
        String gameGroup = getGameGroup(event);

        HashMap<Long, UserGameData> userDataList;

        try {
            userDataList = DBScoreProcess.INSTANCE.getAllUserData(guildId, gameGroup, filter);
        }
        catch (DBConnectException e) {
            event.getHook()
                    .sendMessageEmbeds(e.getErrorEmbed(event.getUserLocale()))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        event.getHook().sendMessageEmbeds(
                getSummaryEmbed(
                        String.format(resourceBundle.getString("rank.entire.embed.summary.title"), filter),
                        userDataList.values().stream().toList(),
                        event.getUserLocale()
                ).build()
        ).queue();
        Logger.addEvent(event);
    }

    @Override
    public void umaReply(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        ResourceBundle resourceBundle = ResourceHandler.getResource(event);

        int filter = getValidFilter(event);
        long guildId = getGuildID(event);
        String gameGroup = getGameGroup(event);

        List<UserGameData> sorted_list = null;

        try {
            sorted_list = getAllSortedList(
                    guildId,
                    gameGroup,
                    filter,
                    UserGameDataComparatorKt::compareWithUma
            );
        }
        catch (DBConnectException e) {
            event.getHook()
                    .sendMessageEmbeds(e.getErrorEmbed(event.getUserLocale()))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        uma_page_count[0] = 1;
        event.getHook().sendMessage(
                getUmaPrintString(
                        sorted_list,
                        String.format(resourceBundle.getString("rank.entire.embed.uma.title"), filter),
                        uma_page_count[0],
                        base64KeyGen(null, null, null, GameType.UMA, filter, null, gameGroup)
                )
        ).addActionRow(
                uma_button[0].asDisabled(),
                uma_button[1].asDisabled(),
                uma_button[2],
                sorted_list.size() > 30 ? uma_button[3] : uma_button[3].asDisabled(),
                sorted_list.size() > 30 ? uma_button[4] : uma_button[4].asDisabled()
        ).queue();
        Logger.addEvent(event);
    }

    @Override
    public void umaPageControl(ButtonInteractionEvent event) {
        ResourceBundle resourceBundle = ResourceHandler.getResource(event);
        int filter = getValidFilter(event);
        long guildId = getButtonGuildID(event);
        String gameGroup = getButtonGameGroup(event);

        List<UserGameData> sorted_list;

        try {
            sorted_list = getAllSortedList(
                    guildId,
                    gameGroup,
                    filter,
                    UserGameDataComparatorKt::compareWithUma
            );
        }
        catch (DBConnectException e) {
            event.getHook()
                    .sendMessageEmbeds(e.getErrorEmbed(event.getUserLocale()))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        pageControl(
                event,
                uma_button,
                uma_page_count,
                sorted_list.size(),
                () -> getUmaPrintString(
                        sorted_list,
                        String.format(resourceBundle.getString("rank.entire.embed.uma.title"), filter),
                        uma_page_count[0],
                        base64KeyGen(null, null, null, GameType.UMA, filter, uma_page_count[0], gameGroup)
                )
        );
        Logger.addEvent(event);
    }

    @Override
    public void totalGameReply(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        ResourceBundle resourceBundle = ResourceHandler.getResource(event);
        int filter = getValidFilter(event);
        long guildId = getGuildID(event);
        String gameGroup = getGameGroup(event);

        List<UserGameData> sorted_list = null;

        try {
            sorted_list = getAllSortedList(
                    guildId,
                    gameGroup,
                    filter,
                    UserGameDataComparatorKt::compareWithGameCount
            );
        }
        catch (DBConnectException e) {
            event.getHook()
                    .sendMessageEmbeds(e.getErrorEmbed(event.getUserLocale()))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        total_game_page_count[0] = 1;
        event.getHook().sendMessage(
                getTotalGamePrintString(
                        sorted_list,
                        String.format(resourceBundle.getString("rank.entire.embed.total_game_count.title"), filter),
                        total_game_page_count[0],
                        base64KeyGen(null, null, null, GameType.GMC, filter, null, gameGroup)
                )
        ).addActionRow(
                total_game_button[0].asDisabled(),
                total_game_button[1].asDisabled(),
                total_game_button[2],
                sorted_list.size() > 30 ? total_game_button[3] : total_game_button[3].asDisabled(),
                sorted_list.size() > 30 ? total_game_button[4] : total_game_button[4].asDisabled()
        ).queue();
        Logger.addEvent(event);
    }

    @Override
    public void totalGamePageControl(ButtonInteractionEvent event) {
        ResourceBundle resourceBundle = ResourceHandler.getResource(event);
        int filter = getValidFilter(event);
        long guildId = getButtonGuildID(event);
        String gameGroup = getButtonGameGroup(event);

        List<UserGameData> sorted_list;

        try {
            sorted_list = getAllSortedList(
                    guildId,
                    gameGroup,
                    filter,
                    UserGameDataComparatorKt::compareWithGameCount
            );
        }
        catch (DBConnectException e) {
            event.getHook()
                    .sendMessageEmbeds(e.getErrorEmbed(event.getUserLocale()))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        pageControl(
                event,
                total_game_button,
                total_game_page_count,
                sorted_list.size(),
                () -> getTotalGamePrintString(
                        sorted_list,
                        String.format(resourceBundle.getString("rank.entire.embed.total_game_count.title"), filter),
                        total_game_page_count[0],
                        base64KeyGen(null, null, null, GameType.GMC, filter, total_game_page_count[0], gameGroup)
                )
        );
        Logger.addEvent(event);
    }
}