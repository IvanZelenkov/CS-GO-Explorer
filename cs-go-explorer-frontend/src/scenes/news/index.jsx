import { useState, useEffect } from "react";
import {Box, CircularProgress, ListItem, ListItemButton, ListItemText} from "@mui/material";
import Header from "../../components/Header";
import { motion } from "framer-motion";
import axios from "axios";
import AccordionSummary from "@mui/material/AccordionSummary";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import Typography from "@mui/material/Typography";
import AccordionDetails from "@mui/material/AccordionDetails";
import Accordion from "@mui/material/Accordion";

const News = () => {
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [news, setNews] = useState([]);

	const onModalClick = (e) => {
		e.stopPropagation();
	};

	const getSteamNews = async () => {
		setInfoLoaded(false);
		try {
			const response = await axios.get(
				"https://" +
				process.env.REACT_APP_REST_API_ID +
				".execute-api.us-east-1.amazonaws.com/ProductionStage/GetCsGoNews"
			);
			setNews(JSON.parse(response.data.body).appnews.newsitems);
			console.log(JSON.parse(response.data.body).appnews.newsitems);
			setInfoLoaded(true);
			// checkIfCsGoStatsExist(JSON.parse(response.data.body), inputSteamId);
		} catch (error) {
			console.log(error);
		}
	}

	useEffect(() => {
		getSteamNews();
	}, []);

	const unixTimeTimestampConverter = (unix_timestamp) => {
		let date = new Date(unix_timestamp * 1000);
		let day = date.getDate();
		let month = date.getMonth() + 1;
		let year = date.getFullYear();
		let hours = date.getHours();
		let minutes = date.getMinutes();
		let seconds = date.getSeconds();

		minutes = minutes.toString().length === 2 ? date.getMinutes() : "0" + date.getMinutes();
		seconds = seconds.toString().length === 2 ? date.getSeconds() : "0" + date.getSeconds();

		// Displays the information in "mm/dd/yyyy - 10:30:23" format
		return month + "/" + day + "/" + year + " - " + hours + ':' + minutes + ':' + seconds;
	}

	if (infoLoaded === false) {
		return (
			<motion.div exit={{ opacity: 0 }}>
				<Box margin="1.5vh">
					<Header title="Currently under development"/>
					<Box sx={{ position: 'absolute', left: '50%', top: '50%', transform: 'translate(-50%, -50%)' }}>
						<CircularProgress color="success"/>
					</Box>
				</Box>
			</motion.div>
		);
	}
	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box margin="1.5vh">
				<Box display="flex" flexDirection="column">
					<Header title="CS:GO News"/>
					<Box style={{ maxHeight: window.innerHeight / 1.5, overflow: 'auto' }}>
						{news?.map((item, gid) => (
							<ListItem key={gid} component="div" disablePadding>
								<ListItemButton>
									<ListItemText primary={
										<a href={item.url} target="_blank"
										    style={{
											 	textDecoration: "none",
											   	color: "#bd2032",
												fontSize: "2vh"
											}}>
											{item.title}
										</a>
									}
								  		secondary={unixTimeTimestampConverter(item.date)}
									/>
								</ListItemButton>
							</ListItem>
						))}
					</Box>
				</Box>
			</Box>
		</motion.div>
	);
};

export default News;