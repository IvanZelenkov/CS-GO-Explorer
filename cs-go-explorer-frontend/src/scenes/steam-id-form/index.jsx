import { useState } from "react";
import { Box, TextField, Button, CircularProgress } from "@mui/material";
import { motion } from "framer-motion";
import { SiValve, SiCounterstrike } from "react-icons/si";
import axios from "axios";

const SteamIdForm = ({ userAccepted, userDenied}) => {
	const [infoLoaded, setInfoLoaded] = useState(true);
	const [inputSteamId, setInputSteamId] = useState("");
	const [isValid, setIsValid] = useState(false);

	const verifySteamID = (input) => {
		const regex = new RegExp("7656[0-9]{13}\\/?");
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
			const response = await axios.get(
				"https://" +
				process.env.REACT_APP_REST_API_ID +
				".execute-api.us-east-1.amazonaws.com/ProductionStage/GetUserStatsForGame?steamid="
				+ inputSteamId
			);
			checkIfCsGoStatsExist(JSON.parse(response.data.body), inputSteamId);
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

	if (infoLoaded === false) {
		return (
			<motion.div exit={{ opacity: 0 }}>
				<Box margin="1.5vh">
					<Box sx={{ position: 'absolute', left: '50%', top: '50%', transform: 'translate(-50%, -50%)' }}>
						<CircularProgress color="success"/>
					</Box>
				</Box>
			</motion.div>
		);
	}
	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box sx={{ display: "flex", justifyContent: "center", marginTop: "10vh" }}>
				<SiValve size="20vh"/>
			</Box>
			<Box
				sx={{
					margin: "1.5vh",
					position: 'absolute',
					left: '50%',
					top: '50%',
					transform: 'translate(-50%, -50%)'
				}}>
				<TextField
					id="outlined-basic"
					label="Steam ID"
					variant="outlined"
					sx={{ width: "20vw" }}
					onChange={(steamId) => verifySteamID(steamId)}
					error={!isValid}
					required={true}
					InputProps={{
						className: "input"
					}}
				/>
				<Button
					onClick={checkUserStats}
					variant="contained"
					sx={{ marginLeft: "2vw", padding: "1vh" }}
					disabled={!isValid}
					className="button"
				>
					<SiCounterstrike size="3vh"/>
				</Button>
			</Box>
		</motion.div>
	);
};

export default SteamIdForm;