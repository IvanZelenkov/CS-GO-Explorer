import { useState, useEffect } from "react";
import { Box, Typography, Link as ProfileLink, useTheme, IconButton, Grid } from "@mui/material";
import { motion } from "framer-motion";
import axios from "axios";
import { tokens } from "../../theme";
import {VolumeOff as VolumeOffIcon, VolumeUp as VolumeUpIcon} from "@mui/icons-material";
import Loader from "../../components/Loader";

const StatHeader = ({ title, textColor }) => {
	return (
		<Typography sx={{
			fontSize: "1.1vh",
			margin: "10px 0 0 0",
			padding: "0.5vh",
			letterSpacing: "0.1vw",
			color: textColor,
			fontWeight: "bold",
			fontFamily: "Montserrat"
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
	const leftCards = [
		{ title: "ROUNDS", userStatsType: "last_match_rounds" },
		{ title: "WINS", userStatsType: "last_match_wins" },
		{ title: "MAX PLAYERS", userStatsType: "last_match_max_players" },
		{ title: "SCORE", userStatsType: "last_match_contribution_score" },
		{ title: "KILLS", userStatsType: "last_match_kills" },
		{ title: "DEATHS", userStatsType: "last_match_deaths" },
		{ title: "MVPs", userStatsType: "last_match_mvps" },
		{ title: "ADR" },
		{ title: "MONEY SPENT" }
	];
	const rightCards = [
		{ title: "MATCHES", userStatsType: "total_matches_played" },
		{ title: "MATCHES WON", userStatsType: "total_matches_won" },
		{ title: "ROUNDS", userStatsType: "total_rounds_played" },
		{ title: "ROUNDS WON", userStatsType: "total_wins" },
		{ title: "AVERAGE PLAYTIME" },
		{ title: "KILLS", userStatsType: "total_kills" },
		{ title: "KNIFE FIGHT KILLS", userStatsType: "total_kills_knife_fight" },
		{ title: "ENEMY BLINDED KILLS", userStatsType: "total_kills_enemy_blinded" },
		{ title: "KILLS AGAINST ZOOMED SNIPER", userStatsType: "total_kills_against_zoomed_sniper" }
	];
	const bottomCards = [
		{ title: "SHOTS", userStatsType: "last_match_favweapon_shots" },
		{ title: "HITS", userStatsType: "last_match_favweapon_hits" },
		{ title: "KILLS", userStatsType: "last_match_favweapon_kills" }
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

	if (infoLoaded === false || profileData === {} || userStats === [])
		return <Loader colors={colors}/>
	return (
		<motion.div exit={{ opacity: 0 }}>
			<video preload={"auto"} autoPlay loop muted={muted} style={{
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
				<Box sx={{ display: "flex", flexDirection: "row" }}>
					{/* LAST MATCH STATS */}
					<Box sx={{ display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center" }}>
						<Box sx={{
							display: "flex",
							alignItems: "center",
							justifyContent: "center",
							backgroundColor: "#1b2838",
							padding: "1.2vh",
							borderRadius: "10px"
						}}>
							<Typography
								fontSize="1.5vh"
								color="custom.steamColorE"
								fontWeight="bold"
								letterSpacing="0.1vw"
								fontFamily="Montserrat"
							>
								LAST MATCH STATS
							</Typography>
						</Box>
						<Grid container className="card-grid">
							{leftCards.map(card => {
								if (card.title === "ADR")
									return (
										<Grid item xs={12} sm={12} md={12} lg={12} xl={4} className="profile-grid-item">
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
										</Grid>
									);
								else if (card.title === "MONEY SPENT")
									return (
										<Grid item xs={12} sm={12} md={12} lg={12} xl={4} className="profile-grid-item">
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
										</Grid>
									);
								else
									return (
										<Grid item xs={12} sm={12} md={12} lg={12} xl={4} className="profile-grid-item">
											<Box className="player-stat-card-left">
												<StatHeader
													title={card.title}
													textColor={"red"}
												/>
												<StatHeader
													title={infoLoaded && userStats[card.userStatsType]}
													textColor={"primary.main"}
												/>
											</Box>
										</Grid>
									);
							})}
						</Grid>
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
							fontSize="2.2vh"
							color="primary.main"
							fontWeight="bold"
							letterSpacing="0.1vw"
							fontFamily="Montserrat"
						>
							{infoLoaded && profileData.response.players[0].personaname}
						</Typography>
						<Typography variant="h5" color="custom.steamColorE" fontWeight="bold" letterSpacing="0.1vw" fontFamily="Montserrat">
							Steam ID: {infoLoaded && profileData.response.players[0].steamid}
						</Typography>
						<Box sx={{ display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", marginTop: "auto" }}>
							<Box
								component="img"
								alt="profile-user"
								src={require("../../assets/images/weapons/" + weaponIdsInOrder[userStats.last_match_favweapon_id - 1] + ".webp")}
								style={{ width: "15vw" }}
							/>
							<Box sx={{
								display: "flex",
								flexDirection: "column",
								alignItems: "center",
								justifyContent: "center",
								backgroundColor: "#1b2838",
								padding: "1.2vh",
								borderRadius: "10px"
							}}>
								<Typography
									fontSize="1.3vh"
									color="gold"
									fontWeight="bold"
									letterSpacing="0.3vw"
									textAlign="center"
									fontFamily="Montserrat"
								>
									LAST MATCH FAVORITE WEAPON
								</Typography>
							</Box>
						</Box>
					</Box>

					{/* TOTAL STATS */}
					<Box sx={{ display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center" }}>
						<Box sx={{
							display: "flex",
							alignItems: "center",
							justifyContent: "center",
							backgroundColor: "#1b2838",
							padding: "1.2vh",
							borderRadius: "10px"
						}}>
							<Typography
								fontSize="1.5vh"
								color="custom.steamColorE"
								fontWeight="bold"
								letterSpacing="0.1vw"
								fontFamily="Montserrat"
							>
								TOTAL STATS
							</Typography>
						</Box>
						<Grid container className="card-grid">
							{rightCards.map(card => {
								if (card.title === "AVERAGE PLAYTIME")
									return (
										<Grid item xs={12} sm={12} md={12} lg={12} xl={4} className="profile-grid-item">
											<Box className="player-stat-card-right">
												<StatHeader
													title={"AVERAGE PLAYTIME"}
													textColor={"red"}
												/>
												<StatHeader
													title={infoLoaded && Math.floor(userStats.total_time_played / 3600) + " hrs"}
													textColor={"primary.main"}
												/>
											</Box>
										</Grid>
									);
								else
									return (
										<Grid item xs={12} sm={12} md={12} lg={12} xl={4} className="profile-grid-item">
											<Box className="player-stat-card-right">
												<StatHeader
													title={card.title}
													textColor={"red"}
												/>
												<StatHeader
													title={infoLoaded && userStats[card.userStatsType]}
													textColor={"primary.main"}
												/>
											</Box>
										</Grid>
									);
							})}
						</Grid>
					</Box>
				</Box>

				{/* LAST MATCH FAVORITE WEAPON STATS */}
				<Box sx={{ display: "flex", flexDirection: "column" }}>
					<Grid
						container
						sx={{ display: "flex", alignItems: "center", justifyContent: "center" }}>
						{bottomCards.map(card => (
							<Grid item className="profile-grid-item">
								<Box className="player-stat-card-bottom">
									<StatHeader
										title={card.title}
										textColor={"red"}
									/>
									<StatHeader
										title={infoLoaded && userStats[card.userStatsType]}
										textColor={"primary.main"}
									/>
								</Box>
							</Grid>
						))}
					</Grid>
					<Box sx={{ display: "flex", justifyContent: "center" }}>
						<IconButton onClick={handleToggleMute}>
							{muted === true ? (
								<VolumeOffIcon sx={{
									color: "white",
									":hover": {
										color: "#66c0f4"
									},
									fontSize: "3vh"
								}}/>
							) : (
								<VolumeUpIcon sx={{
									color: "white",
									":hover": {
										color: "#66c0f4"
									},
									fontSize: "3vh"
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