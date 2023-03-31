import { useState, useEffect } from "react";
import {Box, Stack, Divider, Typography, Link as ProfileLink, useTheme, IconButton} from "@mui/material";
import { motion } from "framer-motion";
import axios from "axios";
import { tokens } from "../../theme";
import loading from "react-useanimations/lib/loading";
import UseAnimations from "react-useanimations";
import {VolumeOff as VolumeOffIcon, VolumeUp as VolumeUpIcon} from "@mui/icons-material";

const StatHeader = ({ title, textColor }) => {
	return (
		<Typography sx={{
			fontSize: "1.3vh",
			margin: "10px 0 0 0",
			padding: "0.5vh",
			letterSpacing: "0.1vw",
			color: textColor,
			fontWeight: "bold"
		}}>
			{title}
		</Typography>
	);
}

const Profile = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [muted, setMuted] = useState(true);
	const [profileData, setProfileData] = useState({});
	const [userStats, setUserStats] = useState([]);
	const generalStatsKeys = [
		"total_kills", "total_deaths", "total_time_played", "total_planted_bombs", "total_defused_bombs",
		"total_wins", "total_damage_done", "total_money_earned", "total_weapons_donated", "total_broken_windows",
		"total_kills_enemy_blinded", "total_kills_knife_fight", "total_kills_against_zoomed_sniper", "total_dominations",
		"total_domination_overkills", "total_revenges", "total_rounds_played", "last_match_t_wins", "last_match_ct_wins",
		"last_match_wins", "last_match_max_players", "last_match_kills", "last_match_deaths", "last_match_mvps",
		"last_match_favweapon_id", "last_match_favweapon_shots", "last_match_favweapon_hits", "last_match_favweapon_kills",
		"last_match_damage", "last_match_money_spent", "last_match_dominations", "last_match_revenges", "total_mvps",
		"total_matches_won", "total_matches_played", "last_match_contribution_score", "last_match_rounds"
	];
	const weaponIdsInOrder = [
		"deagle", "elite", "fiveseven", "glock", "N/A", "N/A", "ak47", "aug", "awp", "famas", "g3sg1", "N/A",
		"galilar", "m249", "N/A", "m4a1", "mac10", "N/A", "p90", "N/A", "N/A", "N/A", "N/A", "ump45", "xm1014",
		"bizon", "mag7", "negev", "sawedoff", "tec9", "taser", "hkp2000", "mp7", "mp9", "nova", "p250", "N/A",
		"scar20", "sg556", "ssg08", "knife", "knife", "N/A", "hegrenade", "N/A", "molotov", "N/A", "N/A",
		"N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "knife", "m4a1", "N/A", "N/A"
	];

	const handleToggleMute = () => setMuted(current => !current);

	const getUserData = async () => {
		try {
			const profileDataResponse = await axios.get("https://" +
				process.env.REACT_APP_REST_API_ID +
				".execute-api.us-east-1.amazonaws.com/ProductionStage/GetPlayerSummaries?steamid="
				+ JSON.parse(localStorage.getItem("steam_id"))
			);
			const userStatsResponse = await axios.get("https://" +
				process.env.REACT_APP_REST_API_ID +
				".execute-api.us-east-1.amazonaws.com/ProductionStage/GetUserStatsForGame?steamid="
				+ JSON.parse(localStorage.getItem("steam_id"))
			);
			setProfileData(JSON.parse(profileDataResponse.data.body));
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
			for (let i = 0; i < generalStatsKeys.length; i++)
				reformattedGeneralStats[generalStatsKeys[i]] = "";

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

	if (infoLoaded === false || profileData === {} || userStats === []) {
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
			<video autoPlay loop muted={muted} style={{
				position: "fixed",
				right: "0",
				bottom: "0",
				minWidth: "100%",
				minHeight: "100%",
				zIndex: -2
			}}>
				<source src={require("../../assets/videos/cs-go-profile-background-video.mp4")} type="video/mp4"/>
			</video>
			<Box margin="1.5vh">
				<Box sx={{ display: "flex", flexDirection: "row", justifyContent: "space-between" }}>
					{/* LAST MATCH STATS */}
					<Box sx={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
						<Typography
							variant="h2"
							color="primary.main"
							fontWeight="bold"
							letterSpacing="0.1vw"
							margin="10px 0 0 0"
						>
							LAST MATCH STATS
						</Typography>
						<Stack spacing={2} className="card-stack">
							{/* ROW #1 */}
							<Box sx={{ display: "flex", flexDirection: "row" }}>
								<Box className="player-stat-card-left">
									<StatHeader
										title={"ROUNDS"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.last_match_rounds}
										textColor={"primary.main"}
									/>
								</Box>
								<Box className="player-stat-card-left">
									<StatHeader
										title={"WINS"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.last_match_wins}
										textColor={"primary.main"}
									/>
								</Box>
								<Box className="player-stat-card-left">
									<StatHeader
										title={"MAX PLAYERS"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.last_match_max_players}
										textColor={"primary.main"}
									/>
								</Box>
							</Box>
							{/* ROW #2 */}
							<Box sx={{ display: "flex", flexDirection: "row" }}>
								<Box className="player-stat-card-left">
									<StatHeader
										title={"SCORE"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.last_match_contribution_score}
										textColor={"primary.main"}
									/>
								</Box>
								<Box className="player-stat-card-left">
									<StatHeader
										title={"KILLS"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.last_match_kills}
										textColor={"primary.main"}
									/>
								</Box>
								<Box className="player-stat-card-left">
									<StatHeader
										title={"DEATHS"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.last_match_deaths}
										textColor={"primary.main"}
									/>
								</Box>
							</Box>
							{/* ROW #3 */}
							<Box sx={{ display: "flex", flexDirection: "row" }}>
								<Box className="player-stat-card-left">
									<StatHeader
										title={"MVPs"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.last_match_mvps}
										textColor={"primary.main"}
									/>
								</Box>
								<Box className="player-stat-card-left">
									<StatHeader
										title={"ADR"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && (userStats.last_match_damage / userStats.last_match_rounds).toFixed(2)}
										textColor={"primary.main"}
									/>
								</Box>
								<Box className="player-stat-card-left">
									<StatHeader
										title={"MONEY SPENT"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && "$" + userStats.last_match_money_spent}
										textColor={"primary.main"}
									/>
								</Box>
							</Box>
						</Stack>
					</Box>

					{/* AVATAR AND LAST MATCH FAVORITE WEAPON IMAGE */}
					<Box sx={{ display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center" }}>
						<Box>
							{infoLoaded && <ProfileLink
								href={profileData.response.players[0].profileurl}
								target="_blank"
								underline="none"
							>
								<Box
									component="img"
									alt="profile-user"
									width="15vh"
									height="15vh"
									src={profileData.response.players[0].avatarfull}
									style={{ cursor: "pointer", borderRadius: "10%" }}
								/>
							</ProfileLink>}
						</Box>
						<Typography
							variant="h2"
							color="primary.main"
							fontWeight="bold"
							letterSpacing="0.1vw"
							margin="10px 0 0 0"
						>
							{infoLoaded && profileData.response.players[0].personaname}
						</Typography>
						<Typography variant="h5" color="custom.steamColorE" fontWeight="bold" letterSpacing="0.1vw">
							Steam ID: {infoLoaded && profileData.response.players[0].steamid}
						</Typography>
						<Box sx={{ display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", marginTop: "auto" }}>
							<Typography variant="h5" color="primary.main" fontWeight="bold" letterSpacing="0.3vw" textAlign="center">
								LAST MATCH
							</Typography>
							<Typography variant="h5" color="primary.main" fontWeight="bold" letterSpacing="0.3vw" textAlign="center">
								FAVORITE WEAPON
							</Typography>
							<Box
								component="img"
								alt="profile-user"
								src={require("../../assets/images/weapons/" + weaponIdsInOrder[userStats.last_match_favweapon_id - 1] + ".webp")}
								style={{ width: "25vh" }}
							/>
						</Box>
					</Box>

					{/* TOTAL STATS */}
					<Box sx={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
						<Typography
							variant="h2"
							color="primary.main"
							fontWeight="bold"
							letterSpacing="0.1vw"
							margin="10px 0 0 0"
						>
							TOTAL STATS
						</Typography>
						<Stack spacing={2} className="card-stack">
							{/* ROW #1 */}
							<Box sx={{ display: "flex", flexDirection: "row" }}>
								<Box className="player-stat-card-right">
									<StatHeader
										title={"MATCHES"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.total_matches_played}
										textColor={"primary.main"}
									/>
								</Box>
								<Box className="player-stat-card-right">
									<StatHeader
										title={"MATCHES WON"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.total_matches_won}
										textColor={"primary.main"}
									/>
								</Box>
								<Box className="player-stat-card-right">
									<StatHeader
										title={"ROUNDS"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.total_rounds_played}
										textColor={"primary.main"}
									/>
								</Box>
							</Box>
							{/* ROW #2 */}
							<Box sx={{ display: "flex", flexDirection: "row" }}>
								<Box className="player-stat-card-right">
									<StatHeader
										title={"ROUNDS WON"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.total_wins}
										textColor={"primary.main"}
									/>
								</Box>
								<Box className="player-stat-card-right">
									<StatHeader
										title={"TOTAL TIME PLAYED"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && Math.floor(userStats.total_time_played / 3600) + " hrs"}
										textColor={"primary.main"}
									/>
								</Box>
								<Box className="player-stat-card-right">
									<StatHeader
										title={"BOMBS PLANTED"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.total_planted_bombs}
										textColor={"primary.main"}
									/>
								</Box>
							</Box>
							{/* ROW #3 */}
							<Box sx={{ display: "flex", flexDirection: "row" }}>
								<Box className="player-stat-card-right">
									<StatHeader
										title={"MVPs"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.total_mvps}
										textColor={"primary.main"}
									/>
								</Box>
								<Box className="player-stat-card-right">
									<StatHeader
										title={"MONEY EARNED"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && "$" + userStats.total_money_earned}
										textColor={"primary.main"}
									/>
								</Box>
								<Box className="player-stat-card-right">
									<StatHeader
										title={"KILLS"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.total_kills}
										textColor={"primary.main"}
									/>
								</Box>
							</Box>
							{/* ROW #3 */}
							<Box sx={{ display: "flex", flexDirection: "row" }}>
								<Box className="player-stat-card-right">
									<StatHeader
										title={"KNIFE FIGHT KILLS"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.total_kills_knife_fight}
										textColor={"primary.main"}
									/>
								</Box>
								<Box className="player-stat-card-right">
									<StatHeader
										title={"ENEMY BLINDED KILLS"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.total_kills_enemy_blinded}
										textColor={"primary.main"}
									/>
								</Box>
								<Box className="player-stat-card-right">
									<StatHeader
										title={"KILLS AGAINST ZOOMED SNIPER"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.total_kills_against_zoomed_sniper}
										textColor={"primary.main"}
									/>
								</Box>
							</Box>
							{/* ROW #4 */}
							<Box sx={{ display: "flex", flexDirection: "row" }}>
								<Box className="player-stat-card-right">
									<StatHeader
										title={"DOMINATIONS"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.total_dominations}
										textColor={"primary.main"}
									/>
								</Box>
								<Box className="player-stat-card-right">
									<StatHeader
										title={"DOMINATION OVERKILLS"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.total_domination_overkills}
										textColor={"primary.main"}
									/>
								</Box>
								<Box className="player-stat-card-right">
									<StatHeader
										title={"WEAPONS DONATED"}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats.total_weapons_donated}
										textColor={"primary.main"}
									/>
								</Box>
							</Box>
						</Stack>
					</Box>
				</Box>

				{/* LAST MATCH FAVORITE WEAPON STATS*/}
				<Box sx={{ display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center" }}>
					<Box sx={{ display: "flex", flexDirection: "row", alignItems: "center", justifyContent: "center" }}>
						<Box className="player-stat-card-bottom">
							<StatHeader
								title={"SHOTS"}
								textColor={"red"}
							/>
							<StatHeader
								title={infoLoaded && userStats.last_match_favweapon_shots}
								textColor={"primary.main"}
							/>
						</Box>
						<Box className="player-stat-card-bottom">
							<StatHeader
								title={"HITS"}
								textColor={"red"}
							/>
							<StatHeader
								title={infoLoaded && userStats.last_match_favweapon_hits}
								textColor={"primary.main"}
							/>
						</Box>
						<Box className="player-stat-card-bottom">
							<StatHeader
								title={"KILLS"}
								textColor={"red"}
							/>
							<StatHeader
								title={infoLoaded && userStats.last_match_favweapon_kills}
								textColor={"primary.main"}
							/>
						</Box>
					</Box>
					<Box sx={{ display: "flex", justifyContent: "center" }}>
						<IconButton onClick={handleToggleMute}>
							{muted === true ? (
								<VolumeOffIcon sx={{
									color: "white",
									":hover": {
										color: "#7da10e"
									},
									fontSize: "5vh"
								}}/>
							) : (
								<VolumeUpIcon sx={{
									color: "white",
									":hover": {
										color: "#7da10e"
									},
									fontSize: "5vh"
								}}/>
							)}
						</IconButton>
					</Box>
				</Box>
			</Box>
		</motion.div>
	);
};

export default Profile;