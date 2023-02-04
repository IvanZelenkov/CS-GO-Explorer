import { useState, useEffect } from "react";
import { Box, Link as ProfileLink, Typography, useTheme } from "@mui/material";
import { motion } from "framer-motion";
import axios from "axios";
import { tokens } from "../../theme";
import loading from "react-useanimations/lib/loading";
import UseAnimations from "react-useanimations";

const StatHeader = ({ title, textColor }) => {
	return (
		<Typography
			variant="h4"
			color={textColor}
			fontWeight="bold"
			fontStyle="italic"
			letterSpacing="0.1vw"
			margin="10px 0 0 0"
		>
			{title}
		</Typography>
	);
}

const Profile = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [profile, setProfile] = useState({});
	const [userStats, setUserStats] = useState({});
	const generalStatsKeys = ["total_kills", "total_deaths", "total_time_played", "total_planted_bombs", "total_defused_bombs",
		"total_wins", "total_damage_done", "total_money_earned", "total_weapons_donated", "total_broken_windows",
		"total_kills_enemy_blinded", "total_kills_knife_fight", "total_kills_against_zoomed_sniper", "total_dominations",
		"total_domination_overkills", "total_revenges", "total_rounds_played", "last_match_t_wins", "last_match_ct_wins",
		"last_match_wins", "last_match_max_players", "last_match_kills", "last_match_deaths", "last_match_mvps",
		"last_match_favweapon_id", "last_match_favweapon_shots", "last_match_favweapon_hits", "last_match_favweapon_kills",
		"last_match_damage", "last_match_money_spent", "last_match_dominations", "last_match_revenges", "total_mvps",
		"total_matches_won", "total_matches_played", "last_match_contribution_score", "last_match_rounds"];

	const getUserData = async () => {
		try {
			const profileResponse =  await axios.get("https://" +
				process.env.REACT_APP_REST_API_ID +
				".execute-api.us-east-1.amazonaws.com/ProductionStage/GetPlayerSummaries?steamid="
				+ JSON.parse(localStorage.getItem("steam_id"))
			);
			const userStatsResponse = await axios.get("https://" +
				process.env.REACT_APP_REST_API_ID +
				".execute-api.us-east-1.amazonaws.com/ProductionStage/GetUserStatsForGame?steamid="
				+ JSON.parse(localStorage.getItem("steam_id"))
			);
			setProfile(JSON.parse(profileResponse.data.body));
			setUserStats(reformatGeneralStatsJson(JSON.parse(userStatsResponse.data.body)));
		} catch (error) {
			console.log(error);
		}
	}

	useEffect(() => {
		getUserData();
	}, []);

	const reformatGeneralStatsJson = (overallStats) => {
		if (overallStats.hasOwnProperty('playerstats')) {
			let reformattedGeneralStats = {};
			for (let i = 0; i < generalStatsKeys.length; i++) {
				reformattedGeneralStats[generalStatsKeys[i]] = "";
				reformattedGeneralStats["value"] = "";
			}

			for (let i = 0; i < overallStats.playerstats.stats.length; i++) {
				let dataItem = overallStats.playerstats.stats[i];

				if (reformattedGeneralStats.hasOwnProperty(dataItem.name))
					reformattedGeneralStats[dataItem.name] = dataItem.value;
			}
			setInfoLoaded(true);
			return reformattedGeneralStats;
		} else {
			setInfoLoaded(false);
			return {};
		}
	}

	if (infoLoaded === false || profile === {} || userStats === {}) {
		return (
			<motion.div exit={{ opacity: 0 }}>
				<Box margin="1.5vh">
					<Box sx={{ position: 'absolute', left: '50%', top: '50%', transform: 'translate(-50%, -50%)' }}>
						<UseAnimations animation={loading} size={50} fillColor={"#7da10e"} strokeColor={"#7da10e"}/>
					</Box>
				</Box>
			</motion.div>
		);
	}
	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box margin="1.5vh">
				<Box>
					<Box display="flex" justifyContent="center" alignItems="center">
						<Box>
							{infoLoaded && <ProfileLink
								href={profile.response.players[0].profileurl}
								target="_blank"
								underline="none"
							>
								<Box
									component="img"
									alt="profile-user"
									width="100px"
									height="100px"
									src={profile.response.players[0].avatarfull}
									style={{ cursor: "pointer", borderRadius: "10%" }}
								/>
							</ProfileLink>}
						</Box>
					</Box>
					<Box textAlign="center">
						<Typography
							variant="h2"
							color="primary.main"
							fontWeight="bold"
							letterSpacing="0.1vw"
							margin="10px 0 0 0"
						>
							{infoLoaded && profile.response.players[0].personaname}
						</Typography>
						<Typography variant="h5" color="custom.steamColorE" fontWeight="bold" letterSpacing="0.1vw">
							Steam ID: {infoLoaded && profile.response.players[0].steamid}
						</Typography>
					</Box>
					<Box display="flex" flexDirection="column" justifyContent="space-between" margin="10vh">
						{/*ROW #1*/}
						<Box display="flex" flexDirection="row" justifyContent="space-between" margin="10vh">
							<Box textAlign="center">
								<StatHeader
									title={"LAST MATCH ROUNDS"}
									textColor={"red"}
								/>
								<StatHeader
									// title={infoLoaded && userStats.total_time_played}
									title={infoLoaded && userStats.last_match_rounds}
									textColor={"primary.main"}
								/>
							</Box>
							<Box textAlign="center">
								<StatHeader
									title={"LAST MATCH T WINS"}
									textColor={"red"}
								/>
								<StatHeader
									title={infoLoaded && userStats.last_match_t_wins}
									textColor={"primary.main"}
								/>
							</Box>
							<Box textAlign="center">
								<StatHeader
									title={"LAST MATCH CT WINS"}
									textColor={"red"}
								/>
								<StatHeader
									title={infoLoaded && userStats.last_match_ct_wins}
									textColor={"primary.main"}
								/>
							</Box>
							<Box textAlign="center">
								<StatHeader
									title={"LAST MATCH MAX PLAYERS"}
									textColor={"red"}
								/>
								<StatHeader
									title={infoLoaded && userStats.last_match_max_players}
									textColor={"primary.main"}
								/>
							</Box>
						</Box>
						{/*ROW #2*/}
						<Box display="flex" flexDirection="row" justifyContent="space-between" margin="10vh">
							<Box textAlign="center">
								<StatHeader
									title={"LAST MATCH KILLS"}
									textColor={"red"}
								/>
								<StatHeader
									title={infoLoaded && userStats.last_match_kills}
									textColor={"primary.main"}
								/>
							</Box>
							<Box textAlign="center">
								<StatHeader
									title={"LAST MATCH DEATHS"}
									textColor={"red"}
								/>
								<StatHeader
									title={infoLoaded && userStats.last_match_deaths}
									textColor={"primary.main"}
								/>
							</Box>
							<Box textAlign="center">
								<StatHeader
									title={"LAST MATCH DAMAGE"}
									textColor={"red"}
								/>
								<StatHeader
									// title={infoLoaded && userStats.total_time_played}
									title={infoLoaded && userStats.last_match_damage}
									textColor={"primary.main"}
								/>
							</Box>
							<Box textAlign="center">
								<StatHeader
									title={"LAST MATCH MVPs"}
									textColor={"red"}
								/>
								<StatHeader
									title={infoLoaded && userStats.last_match_mvps}
									textColor={"primary.main"}
								/>
							</Box>
						</Box>
					</Box>
				</Box>
			</Box>
		</motion.div>
	);
};

export default Profile;