import { useState, useEffect } from "react";
import {Box, CircularProgress, useTheme} from "@mui/material";
import { tokens } from "../../theme";
import { useLocation } from "react-router-dom";
import { motion } from "framer-motion";
import Header from "../../components/Header";
import axios from "axios";

const Profile = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const location = useLocation();
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [userStats, setUserStats] = useState({});

	const getUserStats = () => {
		axios.get(
			"https://" + process.env.REACT_APP_REST_API_ID + ".execute-api.us-east-1.amazonaws.com/ProductionStage/GetUserStatsForGame"
		).then(function (response) {
			setUserStats((JSON.parse(response.data.body)));
			setInfoLoaded(true);
		}).catch(function (error) {
			console.log(error);
		});
	}

	useEffect(() => {
		getUserStats();
	}, []);

	if (infoLoaded === false || userStats.length === 0) {
		return (
			<Box sx={{ position: 'absolute', left: '50%', top: '50%', transform: 'translate(-50%, -50%)' }}>
				<CircularProgress color="success"/>
			</Box>
		);
	}
	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box margin="1.5vh">
				<Header title="YOUR PROFILE"/>

			</Box>
		</motion.div>
	);
};

export default Profile;