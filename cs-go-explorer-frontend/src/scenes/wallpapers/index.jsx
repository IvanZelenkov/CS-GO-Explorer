import { Box, ImageList, ImageListItem } from "@mui/material";
import Header from "../../components/Header";
import { useState } from "react";
import { motion } from "framer-motion";
import UseAnimations from "react-useanimations";
import loading from "react-useanimations/lib/loading";
import DownloadIcon from '@mui/icons-material/Download';

const Wallpapers = () => {
	const [infoLoaded, setInfoLoaded] = useState(true);
	const [hover, setHover] = useState(false);

	if (infoLoaded === false) {
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
						<ImageListItem
							key={0}
							onMouseOver={() => setHover(true)}
							onMouseLeave={() => setHover(false)}
							style={{ marginRight: "0.5vw" }}
						>
							<img
								className={"wallpaper"}
								src={`https://wallpaperaccess.com/full/659198.jpg?w=164&h=164&fit=crop&auto=format`}
								srcSet={`https://wallpaperaccess.com/full/659198.jpg?w=164&h=164&fit=crop&auto=format&dpr=2 2x`}
								alt=""
								loading="lazy"
							/>
							{hover
								? <DownloadIcon
									style={{
										position: "absolute",
										left: '50%',
										top: '50%',
										transform: 'translate(-50%, -50%)',
										cursor: "pointer",
										fontSize: "5vh"

									}}
								/>
								: ""
							}
						</ImageListItem>
						<ImageListItem key={0} style={{ marginRight: "0.5vw" }}>
							<img
								className={"wallpaper"}
								src={`https://wallpaperaccess.com/full/659201.png?w=164&h=164&fit=crop&auto=format`}
								srcSet={`https://wallpaperaccess.com/full/659198.jpg?w=164&h=164&fit=crop&auto=format&dpr=2 2x`}
								alt=""
								loading="lazy"
							/>
						</ImageListItem>
						<ImageListItem key={0} style={{ marginRight: "0.5vw" }}>
							<img
								className={"wallpaper"}
								src={`https://wallpaperaccess.com/full/147188.png?w=164&h=164&fit=crop&auto=format`}
								srcSet={`https://wallpaperaccess.com/full/659198.jpg?w=164&h=164&fit=crop&auto=format&dpr=2 2x`}
								alt=""
								loading="lazy"
							/>
						</ImageListItem>
						<ImageListItem key={0} style={{ marginRight: "0.5vw" }}>
							<img
								className={"wallpaper"}
								src={`https://wallpaperaccess.com/full/659225.jpg?w=164&h=164&fit=crop&auto=format`}
								srcSet={`https://wallpaperaccess.com/full/659198.jpg?w=164&h=164&fit=crop&auto=format&dpr=2 2x`}
								alt=""
								loading="lazy"
							/>
						</ImageListItem>
						<ImageListItem key={0} style={{ marginRight: "0.5vw" }}>
							<img
								className={"wallpaper"}
								src={`https://wallpaperaccess.com/full/634033.png?w=164&h=164&fit=crop&auto=format`}
								srcSet={`https://wallpaperaccess.com/full/659198.jpg?w=164&h=164&fit=crop&auto=format&dpr=2 2x`}
								alt=""
								loading="lazy"
							/>
						</ImageListItem>
						<ImageListItem key={0} style={{ marginRight: "0.5vw" }}>
							<img
								className={"wallpaper"}
								src={`https://wallpaperaccess.com/full/147198.jpg?w=164&h=164&fit=crop&auto=format`}
								srcSet={`https://wallpaperaccess.com/full/659198.jpg?w=164&h=164&fit=crop&auto=format&dpr=2 2x`}
								alt=""
								loading="lazy"
							/>
						</ImageListItem>
						<ImageListItem key={0} style={{ marginRight: "0.5vw" }}>
							<img
								className={"wallpaper"}
								src={`https://wallpaperaccess.com/full/659253.png?w=164&h=164&fit=crop&auto=format`}
								srcSet={`https://wallpaperaccess.com/full/659198.jpg?w=164&h=164&fit=crop&auto=format&dpr=2 2x`}
								alt=""
								loading="lazy"
							/>
						</ImageListItem>
						<ImageListItem key={0} style={{ marginRight: "0.5vw" }}>
							<img
								className={"wallpaper"}
								src={`https://wallpaperaccess.com/full/659261.jpg?w=164&h=164&fit=crop&auto=format`}
								srcSet={`https://wallpaperaccess.com/full/659198.jpg?w=164&h=164&fit=crop&auto=format&dpr=2 2x`}
								alt=""
								loading="lazy"
							/>
						</ImageListItem>
						<ImageListItem key={0} style={{ marginRight: "0.5vw" }}>
							<img
								className={"wallpaper"}
								src={`https://wallpaperaccess.com/full/659379.jpg?w=164&h=164&fit=crop&auto=format`}
								srcSet={`https://wallpaperaccess.com/full/659198.jpg?w=164&h=164&fit=crop&auto=format&dpr=2 2x`}
								alt=""
								loading="lazy"
							/>
						</ImageListItem>
						<ImageListItem key={0} style={{ marginRight: "0.5vw" }}>
							<img
								className={"wallpaper"}
								src={`https://wallpaperaccess.com/full/659398.png?w=164&h=164&fit=crop&auto=format`}
								srcSet={`https://wallpaperaccess.com/full/659198.jpg?w=164&h=164&fit=crop&auto=format&dpr=2 2x`}
								alt=""
								loading="lazy"
							/>
						</ImageListItem>
						<ImageListItem key={0} style={{ marginRight: "0.5vw" }}>
							<img
								className={"wallpaper"}
								src={`https://wallpaperaccess.com/full/260122.jpg?w=164&h=164&fit=crop&auto=format`}
								srcSet={`https://wallpaperaccess.com/full/659198.jpg?w=164&h=164&fit=crop&auto=format&dpr=2 2x`}
								alt=""
								loading="lazy"
							/>
						</ImageListItem>
						<ImageListItem key={0} style={{ marginRight: "0.5vw" }}>
							<img
								className={"wallpaper"}
								src={`https://wallpaperaccess.com/full/659385.jpg?w=164&h=164&fit=crop&auto=format`}
								srcSet={`https://wallpaperaccess.com/full/659198.jpg?w=164&h=164&fit=crop&auto=format&dpr=2 2x`}
								alt=""
								loading="lazy"
							/>
						</ImageListItem>
						<ImageListItem key={0} style={{ marginRight: "0.5vw" }}>
							<img
								className={"wallpaper"}
								src={`https://wallpaperaccess.com/full/659318.png?w=164&h=164&fit=crop&auto=format`}
								srcSet={`https://wallpaperaccess.com/full/659198.jpg?w=164&h=164&fit=crop&auto=format&dpr=2 2x`}
								alt=""
								loading="lazy"
							/>
						</ImageListItem>
						<ImageListItem key={0} style={{ marginRight: "0.5vw" }}>
							<img
								className={"wallpaper"}
								src={`https://wallpaperaccess.com/full/659270.jpg?w=164&h=164&fit=crop&auto=format`}
								srcSet={`https://wallpaperaccess.com/full/659198.jpg?w=164&h=164&fit=crop&auto=format&dpr=2 2x`}
								alt=""
								loading="lazy"
							/>
						</ImageListItem>
						<ImageListItem key={0} style={{ marginRight: "0.5vw" }}>
							<img
								className={"wallpaper"}
								src={`https://wallpaperaccess.com/full/659378.jpg?w=164&h=164&fit=crop&auto=format`}
								srcSet={`https://wallpaperaccess.com/full/659198.jpg?w=164&h=164&fit=crop&auto=format&dpr=2 2x`}
								alt=""
								loading="lazy"
							/>
						</ImageListItem>
					</ImageList>
				</Box>
			</Box>
		</motion.div>
	);
};

export default Wallpapers;