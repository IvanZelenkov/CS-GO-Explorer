import { useState, useEffect } from "react";
import { ProSidebar, Menu, MenuItem } from "react-pro-sidebar";
import { Box, IconButton, Typography, Link as ProfileLink, useTheme } from "@mui/material";
import { GiCrosshair } from "react-icons/gi";
import { MdOutlineMapsHomeWork } from "react-icons/md";
import "react-pro-sidebar/dist/css/styles.css";
import UseAnimations from 'react-useanimations';
import loading from 'react-useanimations/lib/loading';
import ArticleOutlinedIcon from '@mui/icons-material/ArticleOutlined';
import PeopleOutlinedIcon from "@mui/icons-material/PeopleOutlined";
import PersonOutlinedIcon from "@mui/icons-material/PersonOutlined";
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import HelpOutlineOutlinedIcon from "@mui/icons-material/HelpOutlineOutlined";
import MenuOutlinedIcon from "@mui/icons-material/MenuOutlined";
import WallpaperIcon from '@mui/icons-material/Wallpaper';
import YouTubeIcon from '@mui/icons-material/YouTube';
import axios from "axios";
import { motion } from "framer-motion";
import { tokens } from "../../theme";
import SidebarItem from "../../components/SidebarItem";
import SidebarBackgroundImage from "../../assets/images/backgrounds/sidebar_and_tables_background.jpg";
import Loader from "../../components/Loader";

const Sidebar = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const [isCollapsed, setIsCollapsed] = useState(false);
	const [selected, setSelected] = useState("Profile");
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [profile, setProfile] = useState({});

	// Setting local storage for the selected menu item
	useEffect(() => {
		const selectedMenuItem = JSON.parse(localStorage.getItem("selected_menu_item"));
		if (selectedMenuItem)
			setSelected(selectedMenuItem);
	}, []);

	useEffect(() => {
		localStorage.setItem("selected_menu_item", JSON.stringify(selected));
	}, [selected]);

	const getPlayerSummaries = async () => {
		try {
			const response =  await axios.get(
				"https://" +
				process.env.REACT_APP_REST_API_ID +
				".execute-api.us-east-1.amazonaws.com/ProductionStage/GetPlayerSummaries?steamid="
				+ JSON.parse(localStorage.getItem("steam_id"))
			);
			setProfile(JSON.parse(response.data.body));
			setInfoLoaded(true);
		} catch (error) {
			console.log(error);
		}
	}

	useEffect(() => {
		getPlayerSummaries();
	}, []);

	const formatLastTimeOnlineData = () => {
		if (profile.length !== 0 && profile.response.players[0].lastlogoff !== undefined) {
			let unix_timestamp = profile.response.players[0].lastlogoff;
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
	}

	const definePersonaState = () => {
		if (profile.length !== 0) {
			let stateNumber = profile.response.players[0].personastate;
			let communityVisibilityState = profile.response.players[0].communityvisibilitystate;
			if (communityVisibilityState === 3) {
				switch (stateNumber) {
					case 0:
						return "OFFLINE";
					case 1:
						return "ONLINE";
					case 2:
						return "BUSY";
					case 3:
						return "AWAY";
					case 4:
						return "SNOOZE";
					case 5:
						return "LOOKING TO TRADE";
					case 6:
						return "LOOKING TO PLAY"
					default:
						return "OFFLINE";
				}
			} else {
				return "Private";
			}
		}
	}


	if (infoLoaded === false || profile.length === 0)
		return <Loader colors={colors}/>
	return (
		<Box
			sx={{
				"& .pro-sidebar-inner": {
					backgroundImage: `url(${SidebarBackgroundImage}) !important`,
					backgroundSize: "cover",
					backgroundRepeat  : "no-repeat",
					backgroundPosition: "23% 75%"
				},
				"& .pro-icon-wrapper": {
					backgroundColor: "transparent !important"
				},
				"& .pro-inner-item": {
					padding: "0 3vh 0.7vh 2vh !important"
				},
				"& .pro-inner-item:hover": {
					color: `${colors.steamColors[5]} !important`
				},
				"& .pro-menu-item.active": {
					color: `${colors.steamColors[5]} !important`
				},
				".pro-sidebar": {
					height: "100%",
					boxShadow: "15px 0px 50px #5ddcff",
				},
				"@media screen and (max-width: 600px)": {
					"& .pro-sidebar": {
						position: "fixed",
						top: "0",
						left: "0",
						width: "100%",
						height: "100%",
						zIndex: "99",
						background: "white",
						transform: "translateX(-100%)",
						transition: "transform 0.3s ease-in-out",
					},
					"& .pro-sidebar.collapsed": {
						transform: "translateX(-100%)",
					},
					"& .pro-sidebar.collapsed + .overlay": {
						display: "block",
						position: "fixed",
						top: "0",
						left: "0",
						width: "100%",
						height: "100%",
						zIndex: "98",
						background: "rgba(0, 0, 0, 0.5)",
						transition: "opacity 0.3s ease-in-out",
					},
					"& .pro-sidebar .pro-sidebar-inner": {
						height: "calc(100% - 56px)",
						overflowY: "auto",
						overflowX: "hidden",
					},
					"& .pro-sidebar.collapsed .pro-sidebar-inner": {
						height: "100%",
					},
					"& .pro-sidebar .pro-sidebar-inner::-webkit-scrollbar": {
						width: "5px",
					},
					"& .pro-sidebar .pro-sidebar-inner::-webkit-scrollbar-thumb": {
						background: `${colors.steamColors[0]} !important`,
					},
					"& .pro-sidebar .pro-sidebar-inner::-webkit-scrollbar-track": {
						background: "transparent",
					},
					"& .pro-sidebar .pro-menu-item": {
						color: `${colors.steamColors[0]} !important`,
						padding: "10px",
						borderRadius: "5px",
					}
				},
				zIndex: "0"
			}}
		>
			<ProSidebar collapsed={isCollapsed} width="100%">
				<Menu iconShape="square">
					<Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
						{!isCollapsed && <MenuItem>
							<Box style={{ cursor: "auto" }}>
								<Typography
									color="custom.steamColorD"
									letterSpacing="0.1vw"
									fontWeight="bold"
									fontSize="1.7vh"
									fontFamily="Montserrat"
								>
									Status: {infoLoaded && definePersonaState()}
								</Typography>
								<Typography
									color="custom.steamColorD"
									fontSize="1.4vh"
									fontFamily="Montserrat"
								>
									{infoLoaded && formatLastTimeOnlineData()}
								</Typography>
							</Box>
						</MenuItem>}
						<MenuItem
							onClick={() => setIsCollapsed(!isCollapsed)}
							icon={isCollapsed ? <MenuOutlinedIcon/> : undefined}
							style={{ margin: "1.5vh 0 1vh 0", color: colors.steamColors[4] }}
						>
							{!isCollapsed && (
								<Box
									display="flex"
									justifyContent="space-between"
									alignItems="center"
									marginLeft="1vw"
								>
									<IconButton onClick={() => setIsCollapsed(!isCollapsed)}>
										<MenuOutlinedIcon sx={{ color: "custom.steamColorD" }}/>
									</IconButton>
								</Box>
							)}
						</MenuItem>
					</Box>

					{!isCollapsed && (
						<Box marginBottom="25px">
							<Box display="flex" justifyContent="center" alignItems="center">
								<MenuItem>
									{infoLoaded && <ProfileLink
										href={profile.response.players[0].profileurl}
										target="_blank"
										underline="none"
									>
										<Box
											component="img"
											alt="profile-user"
											width="100px"
											height="100px"
											src={profile.response.players[0].avatarfull}
											style={{ cursor: "pointer", borderRadius: "10%" }}
										/>
									</ProfileLink>}
								</MenuItem>
							</Box>
							<Box textAlign="center">
								<Typography
									fontSize="2vh"
									color="primary.main"
									fontWeight="bold"
									letterSpacing="0.1vw"
									margin="10px 0 0 0"
									fontFamily="Montserrat"
								>
									{infoLoaded && profile.response.players[0].personaname}
								</Typography>
								<Typography
									fontSize="1.2vh"
									color="custom.steamColorE"
									fontWeight="bold"
									letterSpacing="0.1vw"
									fontFamily="Montserrat"
								>
									Steam ID: {infoLoaded && profile.response.players[0].steamid}
								</Typography>
							</Box>
						</Box>
					)}

					{/* MENU ITEMS */}
					<Box paddingLeft={isCollapsed ? undefined : "10%"}>
						{/*General*/}
						<Typography
							color="custom.steamColorE"
							fontWeight="bold"
							fontSize="1.1vh"
							margin="15px 0 5px 15px"
							fontFamily="Montserrat"
						>
							General
						</Typography>
						<motion.div whileHover={{ scale: 1.1 }}>
							<SidebarItem
								title="Profile"
								to="/profile"
								icon={<PersonOutlinedIcon/>}
								selected={selected}
								setSelected={setSelected}
							/>
						</motion.div>
						<motion.div whileHover={{ scale: 1.1 }}>
							<SidebarItem
								title="Friends"
								to="/friends"
								icon={<PeopleOutlinedIcon/>}
								selected={selected}
								setSelected={setSelected}
							/>
						</motion.div>
						<motion.div whileHover={{ scale: 1.1 }}>
							<SidebarItem
								title="News"
								to="/news"
								icon={<ArticleOutlinedIcon/>}
								selected={selected}
								setSelected={setSelected}
							/>
						</motion.div>
						<motion.div whileHover={{ scale: 1.1 }}>
							<SidebarItem
								title="Videos"
								to="/video-platform"
								icon={<YouTubeIcon size={20}/>}
								selected={selected}
								setSelected={setSelected}
							/>
						</motion.div>
						<motion.div whileHover={{ scale: 1.1 }}>
							<SidebarItem
								title="4K Wallpapers"
								to="/wallpapers"
								icon={<WallpaperIcon/>}
								selected={selected}
								setSelected={setSelected}
							/>
						</motion.div>

						{/*Statistics*/}
						<Typography
							color="custom.steamColorE"
							fontWeight="bold"
							fontSize="1.1vh"
							margin="15px 0 5px 15px"
							fontFamily="Montserrat"
						>
							Stats
						</Typography>
						<motion.div whileHover={{ scale: 1.1 }}>
							<SidebarItem
								title="Weapons"
								to="/weapon-stats"
								icon={<GiCrosshair size={23}/>}
								selected={selected}
								setSelected={setSelected}
							/>
						</motion.div>
						<motion.div whileHover={{ scale: 1.1 }}>
							<SidebarItem
								title="Maps"
								to="/map-stats"
								icon={<MdOutlineMapsHomeWork size={20}/>}
								selected={selected}
								setSelected={setSelected}
							/>
						</motion.div>

						{/*Guides*/}
						<Typography
							color="custom.steamColorE"
							fontWeight="bold"
							fontSize="1.1vh"
							margin="15px 0 5px 15px"
							fontFamily="Montserrat"
						>
							Guides
						</Typography>
						<motion.div whileHover={{ scale: 1.1 }}>
							<SidebarItem
								title="Calendar"
								to="/calendar"
								icon={<CalendarMonthIcon/>}
								selected={selected}
								setSelected={setSelected}
							/>
						</motion.div>
						<motion.div whileHover={{ scale: 1.1 }}>
							<SidebarItem
								title="FAQ Page"
								to="/faq"
								icon={<HelpOutlineOutlinedIcon/>}
								selected={selected}
								setSelected={setSelected}
							/>
						</motion.div>
					</Box>
				</Menu>
			</ProSidebar>
		</Box>
	);
};

export default Sidebar;