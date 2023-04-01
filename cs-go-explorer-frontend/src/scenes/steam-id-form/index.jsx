import { useState } from "react";
import { Box, TextField, Button, IconButton } from "@mui/material";
import { motion } from "framer-motion";
import { SiValve, SiCounterstrike } from "react-icons/si";
import axios from "axios";
import loading from "react-useanimations/lib/loading";
import UseAnimations from "react-useanimations";
import { VolumeUp as VolumeUpIcon, VolumeOff as VolumeOffIcon } from '@mui/icons-material';

const SteamIdForm = ({ userAccepted, userDenied}) => {
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
					justifyContent: "center",
					marginTop: "10vh",
					position: "absolute",
					top: "30%",
					left: "50%",
					transform: "translate(-50%, -50%)"
				}}>
					<SiValve size="20vh"/>
				</Box>
				<Box
					sx={{
						display: "flex",
						flexDirection: "row",
						position: "absolute",
						left: "50%",
						top: "50%",
						transform: "translate(-50%, -50%)"
					}}>
					<TextField
						required
						id="outlined-basic"
						label="Steam ID"
						variant="outlined"
						sx={{ width: "20vw" }}
						onChange={(steamId) => verifySteamID(steamId)}
						error={invalidSteamIdMessage !== ""}
						helperText={invalidSteamIdMessage}
						InputProps={{
							className: "input"
						}}
						onKeyDown={handleKeyDown}
					/>
					{infoLoaded === true ?
						<Button
							onClick={checkUserStats}
							variant="contained"
							sx={{ marginLeft: "2vw", padding: "1vh" }}
							disabled={!isValid}
							className="button"
						>
							<SiCounterstrike size="3vh"/>
						</Button>
						:
						<Box sx={{ marginLeft: "2vw" }}>
							<UseAnimations animation={loading} size={50} fillColor={"#7da10e"} strokeColor={"#7da10e"}/>
						</Box>
					}
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
		</motion.div>
	);
};

export default SteamIdForm;