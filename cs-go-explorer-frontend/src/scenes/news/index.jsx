import { useState, useEffect } from "react";
import { Box, ImageList, ImageListItem, Pagination, Typography, useTheme } from "@mui/material";
import Header from "../../components/Header";
import { motion } from "framer-motion";
import axios from "axios";
import { muiPaginationCSS, tokens } from "../../theme";
import Loader from "../../components/Loader";

const News = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [news, setNews] = useState([]);
	const [reformattedNews, setReformattedNews] = useState([]);
	const [page, setPage] = useState(1);
	const newsPerPage = 6;
	const totalNews = Math.ceil(reformattedNews.flat().length / newsPerPage);

	const handleChange = (event, value) => {
		setPage(value);
	};

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

	if (infoLoaded === false || news.length === 0)
		return <Loader colors={colors}/>
	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box sx={{
				display: "flex",
				flexDirection: "column",
				margin: "1.5vh"
			}}>
				<Header title="CS:GO News" subtitle="Explore latest news"/>
				<Box sx={{
					display: "flex",
					flexDirection: "column",
					justifyContent: "space-between",
					alignItems: "center",
					height: "calc(100vh - 20vh)",
					overflowY: "auto",
				}}>
					<ImageList cols={3} gap={40} sx={{ width: "90%", padding: "10px" }}>
						{reformattedNews?.slice((page - 1) * newsPerPage, page * newsPerPage).map((news) => (
							<ImageListItem
								key={news.gid}
								sx={{
									marginRight: "0.5vw",
									textAlign: "center",
									border: `0.2vh solid ${colors.steamColors[6]}`,
									borderRadius: "1vh",
									boxShadow: `0px 0px 10px ${colors.steamColors[5]}`,
								}}
							>
								<img
									className={"news-image"}
									src={`${news.imgSource}?w=164&h=164&fit=crop&auto=format`}
									srcSet={`${news.imgSource}?w=164&h=164&fit=crop&auto=format&dpr=2 2x`}
									alt=""
									loading="lazy"
									onClick={() => window.open(news.hyperLinkHref, "_blank")}
								/>
								<Box sx={{
									backgroundColor: colors.steamColors[1],
									borderBottomRightRadius: "1vh",
									borderBottomLeftRadius: "1vh"
								}}>
									<Typography sx={{
										margin: "1.1vh",
										fontSize: "1vh",
										fontFamily: "Montserrat",
										fontWeight: "600",
										color: colors.steamColors[5]
									}}>
										{news.title}
									</Typography>
								</Box>
							</ImageListItem>
						))}
					</ImageList>
					<Pagination
						count={totalNews}
						page={page}
						onChange={handleChange}
						sx={muiPaginationCSS}
					/>
				</Box>
			</Box>
		</motion.div>
	);
};

export default News;