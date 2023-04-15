import { useState, useEffect } from "react";
import {Box, ImageList, ImageListItem, useTheme} from "@mui/material";
import Header from "../../components/Header";
import { motion } from "framer-motion";
import UseAnimations from "react-useanimations";
import loading from "react-useanimations/lib/loading";
import download from "react-useanimations/lib/download";
import { RxEnterFullScreen } from 'react-icons/rx';
import axios from "axios";
import { tokens} from "../../theme";
import { saveAs } from "file-saver";
import Loader from "../../components/Loader";

const Wallpapers = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [wallpapers, setWallpapers] = useState([]);

	const Image = ({ imageUrl, index }) => {
		return (
			<ImageListItem key={index} style={{ marginRight: "0.5vw" }}>
				<img
					id={imageUrl}
					src={`${imageUrl}?w=164&h=164&fit=crop&auto=format`}
					alt="cs-go-image"
					loading="lazy"
					className="wallpaper"
				/>
				<Box sx={{
					display: "flex",
					flexDirection: "row",
					borderLeft: `0.2vh solid ${colors.steamColors[5]}`,
					borderBottom: `0.2vh solid ${colors.steamColors[5]}`,
					borderRight: `0.2vh solid ${colors.steamColors[5]}`,
				}}>
					<Box sx={{
						display: "flex",
						justifyContent: "center",
						width: "100%",
						borderRight: `0.1vh solid ${colors.steamColors[5]}`
					}}>
						<RxEnterFullScreen
							className="wallpapers-icons"
							onClick={() => document.getElementById(imageUrl)?.requestFullscreen()}
						/>
					</Box>
					<Box sx={{
						display: "flex",
						justifyContent: "center",
						width: "100%",
						borderLeft: `0.1vh solid ${colors.steamColors[5]}`
					}}>
						<UseAnimations
							animation={download}
							fillColor={colors.steamColors[6]}
							strokeColor="white"
							className="wallpapers-icons"
							onClick={() => saveImage(imageUrl)}
						/>
					</Box>
				</Box>
			</ImageListItem>
		);
	}

	const getWallpapers = async () => {
		try {
			const response = await axios.get(
				"https://" +
				process.env.REACT_APP_REST_API_ID +
				".execute-api.us-east-1.amazonaws.com/ProductionStage/GetCsGoWallpapers"
			);
			setWallpapers(JSON.parse(response.data.body));
			setInfoLoaded(true);
		} catch (error) {
			console.log(error);
		}
	}

	const saveImage = (imageUrl) => {
		const imageExtension = imageUrl.toString().match("\\.\\w{3,4}($|\\?)");
		(async () => {
			let name = 'cs-go-wallpaper' + Math.floor(Math.random() * 900000) + 100000 + imageExtension[0];
			let blob = await fetch(imageUrl).then((response) => response.blob());
			saveAs(blob, name);
		})();
	}

	useEffect(() => {
		getWallpapers();
	}, []);

	if (infoLoaded === false || wallpapers.length === 0)
		return <Loader colors={colors}/>
	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box margin="1.5vh">
				<Box display="flex" flexDirection="column">
					<Header title="4k Wallpapers" subtitle="Explore and download 4k wallpapers"/>
					<ImageList sx={{ width: "100%", height: "79vh" }} cols={5} gap={40}>
						{wallpapers?.map((imageUrl, index) => (
							<Image imageUrl={imageUrl} key={index}/>
						))}
					</ImageList>
				</Box>
			</Box>
		</motion.div>
	);
};

export default Wallpapers;