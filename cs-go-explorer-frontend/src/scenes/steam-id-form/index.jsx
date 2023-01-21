import { useState, useEffect } from "react";
import {Box, TextField, Button, CircularProgress} from "@mui/material";
import Header from "../../components/Header";
import { motion } from "framer-motion";
import { SiValve } from "react-icons/si";
import axios from "axios";

const SteamIdForm = ({ userAcceptedFunction, userDeniedFunction }) => {
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [steamId, setSteamId] = useState("");
	const [isValid, setIsValid] = useState(false);
	const [userStats, setUserStats] = useState({});

	const verifySteamID = (input) => {
		const regex = new RegExp("\\d{17}");
		if (regex.test(input.target.value)) {
			setIsValid(true);
			setSteamId(input.target.value);
			setInfoLoaded(true);
		}
	}

	const getUserStats = async () => {
		try {
			const response = await axios.get(
				"https://" +
				process.env.REACT_APP_REST_API_ID +
				".execute-api.us-east-1.amazonaws.com/ProductionStage/GetUserStatsForGame?steamid=" + steamId
			);
			setUserStats(checkIfCsGoStatsExist(JSON.parse(response.data.body)));
		} catch (error) {
			console.log(error);
		}
	}

	const checkIfCsGoStatsExist = (overallStats) => {
		if (overallStats.hasOwnProperty('playerstats')) {
			if (overallStats.playerstats.hasOwnProperty("stats")) {
				if (overallStats.playerstats.stats.length !== 0) {
					setInfoLoaded(true);
					return true;
				}
			}
		} else {
			setInfoLoaded(false);
			return false;
		}
	}

	//
	// if (infoLoaded === true) {
	// 	return (
	// 		<motion.div exit={{ opacity: 0 }}>
	// 			<Box margin="1.5vh">
	// 				<Box sx={{ position: 'absolute', left: '50%', top: '50%', transform: 'translate(-50%, -50%)' }}>
	// 					<CircularProgress color="success"/>
	// 				</Box>
	// 			</Box>
	// 		</motion.div>
	// 	);
	// }
	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box margin="1.5vh">
				<Box display="flex" justifyContent="space-between" alignItems="center">
					<Header title="Steam ID Form"/>
					<Box sx={{ position: 'absolute', left: '50%', top: '50%', transform: 'translate(-50%, -50%)' }}>
						<TextField
							id="outlined-basic"
							label="Steam ID"
							variant="outlined"
							sx={{ width: "20vw" }}
							onChange={(steamId) => verifySteamID(steamId)}
							error={!isValid}
							required={true}
						/>
						<Button
							onClick={() => getUserStats() === true ? console.log("e") : userDeniedFunction()}
							variant="contained"
							sx={{ marginLeft: "3vw", padding: "0" }}
							disabled={!isValid}
						>
							<SiValve size={50}/>
						</Button>
					</Box>
				</Box>
			</Box>
		</motion.div>
	);
};

export default SteamIdForm;