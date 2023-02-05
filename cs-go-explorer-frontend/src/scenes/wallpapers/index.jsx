import { Box, ImageList, ImageListItem } from "@mui/material";
import Header from "../../components/Header";
import { useState, useEffect } from "react";
import { motion } from "framer-motion";
import UseAnimations from "react-useanimations";
import loading from "react-useanimations/lib/loading";
import { MdOutlineDownloading }  from 'react-icons/md';
import axios from "axios";

const Wallpapers = () => {
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [wallpapers, setWallpapers] = useState([]);

	const Image = ({ imageUrl, index }) => {
		const [hover, setHover] = useState(false);
		return (
			<ImageListItem key={index} style={{ marginRight: "0.5vw" }}>
				<img
					src={`${imageUrl}?w=164&h=164&fit=crop&auto=format`}
					alt=""
					loading="lazy"
					className="wallpaper"
					onMouseOver={() => setHover(true)}
					onMouseLeave={() => setHover(false)}
				/>
				{hover ?
					<MdOutlineDownloading
						className="download-icon"
						onMouseOver={() => setHover(true)}
						onClick={e => downloadImage(e, imageUrl)}
					/> :
					""
				}
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

	useEffect(() => {
		getWallpapers();
	}, []);

	const downloadImage = (e, imageUrl) => {
		console.log(e.target.href);
		fetch(e.target.href, {
			method: "GET",
			headers: {}
		}).then(response => {
				response.arrayBuffer().then(function(buffer) {
					const url = window.URL.createObjectURL(new Blob([buffer]));
					const link = document.createElement("a");
					link.href = url;
					link.setAttribute("download", "image.png"); //or any other extension
					document.body.appendChild(link);
					link.click();
				});
		}).catch(error => {
			console.log(error);
		});
	};

	if (infoLoaded === false || wallpapers.length === 0) {
		return (
			<motion.div exit={{ opacity: 0 }}>
				<Box margin="1.5vh">
					<Header title="4K Wallpapers" subtitle="Explore and download 4k wallpapers"/>
					<Box sx={{ position: 'absolute', left: '50%', top: '50%', transform: 'translate(-50%, -50%)' }}>
						<UseAnimations animation={loading} size={50} fillColor={"#7da10e"} strokeColor={"#7da10e"}/>
					</Box>
				</Box>
			</motion.div>
		);
	}
	return (
		// MOCK DATA
		<motion.div exit={{ opacity: 0 }}>
			<Box margin="1.5vh">
				<Box display="flex" flexDirection="column">
					<Header title="4k Wallpapers" subtitle="Explore and download 4k wallpapers"/>
					<ImageList sx={{ width: "100%", height: "79vh" }} cols={5} gap={40}>
						{wallpapers?.map((imageUrl, index) => (
							<Image imageUrl={imageUrl} index={index}/>
						))}
					</ImageList>
				</Box>
			</Box>
		</motion.div>
	);
};

export default Wallpapers;