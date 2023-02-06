import { useState, useEffect } from "react";
import { Box, ImageList, ImageListItem, Typography, useTheme } from "@mui/material";
import Header from "../../components/Header";
import { motion } from "framer-motion";
import axios from "axios";
import loading from "react-useanimations/lib/loading";
import UseAnimations from "react-useanimations";
import { tokens } from "../../theme";

const News = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [news, setNews] = useState([]);
	const [reformattedNews, setReformattedNews] = useState([]);

	useEffect(() => {
		getSteamNews();
	}, []);

	const getSteamNews = async () => {
		setInfoLoaded(false);
		try {
			const response = await axios.get(
				"https://" +
				process.env.REACT_APP_REST_API_ID +
				".execute-api.us-east-1.amazonaws.com/ProductionStage/GetNewsForApp"
			);
			setNews(JSON.parse(response.data.body).appnews.newsitems);
			getHtmlTags(JSON.parse(response.data.body).appnews.newsitems);
			setInfoLoaded(true);
		} catch (error) {
			console.log(error);
		}
	}

	const getHtmlTags = (newsItems) => {
		newsItems.map((item) => {
			console.log(item)
			let imgSource = item.contents.match(/<img [^>]*src="[^"]*"[^>]*>/gm);
			if (imgSource != null) {
				imgSource = imgSource.map(src => src.replace(/.*src="([^"]*)".*/, '$1'));
				reformattedNews.push({
					gid: item.gid,
					author: item.author,
					title: item.title,
					hyperLinkHref: item.url,
					imgSource: imgSource
				});
			}
		});
	}

	if (infoLoaded === false || news.length === 0) {
		return (
			<motion.div exit={{ opacity: 0 }}>
				<Box margin="1.5vh">
					<Header title="CS:GO News" subtitle="Explore latest news"/>
					<Box sx={{ position: 'absolute', left: '50%', top: '50%', transform: 'translate(-50%, -50%)' }}>
						<UseAnimations animation={loading} size={50} fillColor={"#7da10e"} strokeColor={"#7da10e"}/>
					</Box>
				</Box>
			</motion.div>
		);
	}
	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box margin="1.5vh">
				<Box display="flex" flexDirection="column">
					<Header title="CS:GO News" subtitle="Explore latest news"/>
					<ImageList sx={{ width: "100%", height: "80vh" }} cols={5} gap={40}>
						{reformattedNews?.map((item) => (
							<ImageListItem
								key={item.gid}
								style={{
									marginRight: "0.5vw",
									textAlign: "center",
									border: `0.2vh solid ${colors.steamColors[6]}`,
									borderRadius: "1vh",
								}}
							>
								<img
									className={"news-image"}
									src={`${item.imgSource}?w=164&h=164&fit=crop&auto=format`}
									srcSet={`${item.imgSource}?w=164&h=164&fit=crop&auto=format&dpr=2 2x`}
									alt=""
									loading="lazy"
									onClick={() => window.open(item.hyperLinkHref, "_blank")}
								/>
								<Box>
									<Typography sx={{ margin: "1vh", fontSize: "1vh" }}>
										{item.title}
									</Typography>
								</Box>
							</ImageListItem>
						))}
					</ImageList>
				</Box>
			</Box>
		</motion.div>
	);
};

export default News;