import { useState } from "react";
import {Box, TextField, Button, IconButton, useTheme, Tooltip} from "@mui/material";
import { motion } from "framer-motion";
import { SiValve, SiCounterstrike } from "react-icons/si";
import axios from "axios";
import loading from "react-useanimations/lib/loading";
import UseAnimations from "react-useanimations";
import { VolumeUp as VolumeUpIcon, VolumeOff as VolumeOffIcon } from '@mui/icons-material';
import { muiTextFieldCSS, tokens } from "../../theme";

const SteamIdForm = ({ userAccepted, userDenied}) => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const [infoLoaded, setInfoLoaded] = useState(true);
	const [inputSteamId, setInputSteamId] = useState("");
	const [isValid, setIsValid] = useState(false);
	const [muted, setMuted] = useState(true);
	const [invalidSteamIdMessage, setInvalidSteamIdMessage] = useState("");

	const handleToggleMute = () => setMuted(current => !current);

	const handleKeyDown = (event) => {
		if (event.key === "Enter") {
			checkUserStats();
		}
	}

	const verifySteamID = (input) => {
		setInfoLoaded(true);
		setInvalidSteamIdMessage("");
		const regex = new RegExp("7656[0-9]{13}$");
		if (regex.test(input.target.value)) {
			setIsValid(true);
			setInputSteamId(input.target.value);
		} else {
			setIsValid(false);
		}
	}

	const checkUserStats = async () => {
		setInfoLoaded(false);
		try {
			await axios.get(
				"https://" +
				process.env.REACT_APP_REST_API_ID +
				".execute-api.us-east-1.amazonaws.com/ProductionStage/GetUserStatsForGame?steamid="
				+ inputSteamId
			).then(response => {
				checkIfCsGoStatsExist(JSON.parse(response.data.body), inputSteamId);
			}).catch(() => {
				setInvalidSteamIdMessage("This Steam ID does not exist.")
			})
		} catch (error) {
			console.log(error);
		}
	}

	const checkIfCsGoStatsExist = (overallStats, steamId) => {
		if (overallStats.hasOwnProperty('playerstats')) {
			if (overallStats.playerstats.hasOwnProperty("stats")) {
				if (overallStats.playerstats.stats.length !== 0) {
					setInfoLoaded(false);
					localStorage.setItem("is_user_allowed", "accept");
					localStorage.setItem("steam_id", JSON.stringify(steamId));
					userAccepted(localStorage.getItem("is_user_allowed"));
					setInfoLoaded(true);
				}
			}
		} else {
			setInfoLoaded(false);
			localStorage.setItem("is_user_allowed", "deny");
			userDenied(localStorage.getItem("is_user_allowed"));
			setInfoLoaded(true);
			alert("The player with a Steam ID: " + inputSteamId + " does not have any stats in CS:GO.");
		}
	}

	return (
		<motion.div exit={{ opacity: 0 }}>
			<video preload={"auto"} autoPlay loop muted={muted} style={{
				position: "fixed",
				right: "0",
				bottom: "0",
				minWidth: "100%",
				minHeight: "100%"
			}}>
				<source src={require("../../assets/videos/cs-go-steam-id-form-background-video.mp4")} type="video/mp4"/>
			</video>
			<Box sx={{
				display: "flex",
				flexDirection: "column"
			}}>
				<Box sx={{
					display: "flex",
					flexDirection: "column",
					justifyContent: "center",
					alignItems: "center",
					position: "absolute",
					left: "50%",
					top: "45%",
					transform: "translate(-50%, -50%)"
				}}>
					<Box
						component="img"
						alt="cs-go-logo"
						sx={{
							maxWidth: "70%",
							height: "auto",
							marginBottom: "2vh"
						}}
						src={require("../../assets/images/logo/cs-go-explorer-logo.png")}
					/>
					<Box
						sx={{
							display: "flex",
							flexDirection: "row"
						}}>
						<TextField
							required
							id="outlined-basic"
							label="Steam ID"
							variant="outlined"
							onChange={(steamId) => verifySteamID(steamId)}
							error={invalidSteamIdMessage !== ""}
							helperText={invalidSteamIdMessage}
							onKeyDown={handleKeyDown}
							sx={muiTextFieldCSS("#5ddcff")}
							inputProps={{ style: { fontFamily: "Montserrat" }}}
							inputlabelprops={{ style: { fontFamily: "Montserrat" }}}
						/>
						{infoLoaded === true ?
							<Button
								onClick={checkUserStats}
								variant="contained"
								sx={{
									marginLeft: "2vw",
									padding: "1vh",
									borderRadius: "5px",
									height: "3.3rem",
									boxShadow: "0px 0px 10px #5ddcff"
								}}
								disabled={!isValid}
							>
								<SiCounterstrike size="36px"/>
							</Button>
							:
							<Box sx={{ marginLeft: "2vw" }}>
								<UseAnimations animation={loading} size={50} fillColor={colors.steamColors[6]} strokeColor={colors.steamColors[6]}/>
							</Box>
						}
					</Box>
				</Box>
			</Box>
			<Box sx={{
				display: "flex",
				justifyContent: "center",
				marginTop: "10vh",
				position: "absolute",
				top: "83%",
				left: "50%",
				transform: "translate(-50%, -50%)",
			}}>
				{muted === true ? (
					<Tooltip title="Dark Theme" placement="bottom">
						<IconButton onClick={handleToggleMute} sx={{
							border: `0.2vh solid #5ddcff`,
							boxShadow: "0px 0px 10px #5ddcff"
						}}>
							<VolumeOffIcon sx={{
								color: colors.steamColors[4],
								":hover": {
									color: colors.steamColors[5]
								},
								fontSize: "3vh"
							}}/>
						</IconButton>
					</Tooltip>
				) : (
					<Tooltip title="Dark Theme" placement="bottom">
						<IconButton onClick={handleToggleMute} sx={{
							border: `0.2vh solid #5ddcff`,
							boxShadow: "0px 0px 10px #5ddcff"
						}}>
							<VolumeUpIcon sx={{
								color: colors.steamColors[4],
								":hover": {
									color: colors.steamColors[5]
								},
								fontSize: "3vh"
							}}/>
						</IconButton>
					</Tooltip>
				)}
			</Box>
		</motion.div>
	);
};

export default SteamIdForm;