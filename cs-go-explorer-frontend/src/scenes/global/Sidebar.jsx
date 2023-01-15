import { useState, useEffect } from "react";
import { ProSidebar, Menu, MenuItem } from "react-pro-sidebar";
import {Box, IconButton, Typography, Link as ProfileLink, useTheme, CircularProgress} from "@mui/material";
import { Link as SidebarLink } from "react-router-dom";
import "react-pro-sidebar/dist/css/styles.css";
import { tokens } from "../../theme";
import HomeOutlinedIcon from "@mui/icons-material/HomeOutlined";
import PeopleOutlinedIcon from "@mui/icons-material/PeopleOutlined";
import ContactsOutlinedIcon from "@mui/icons-material/ContactsOutlined";
import ReceiptOutlinedIcon from "@mui/icons-material/ReceiptOutlined";
import PersonOutlinedIcon from "@mui/icons-material/PersonOutlined";
import CalendarTodayOutlinedIcon from "@mui/icons-material/CalendarTodayOutlined";
import HelpOutlineOutlinedIcon from "@mui/icons-material/HelpOutlineOutlined";
import BarChartOutlinedIcon from "@mui/icons-material/BarChartOutlined";
import PieChartOutlineOutlinedIcon from "@mui/icons-material/PieChartOutlineOutlined";
import TimelineOutlinedIcon from "@mui/icons-material/TimelineOutlined";
import MenuOutlinedIcon from "@mui/icons-material/MenuOutlined";
import MapOutlinedIcon from "@mui/icons-material/MapOutlined";
import axios from "axios";

const Item = ({ title, to, icon, selected, setSelected }) => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	return (
		<MenuItem
			active={selected === title}
			style={{
				color: colors.steamColors[4]
			}}
			onClick={() => setSelected(title)}
			icon={icon}
		>
			<Typography>{title}</Typography>
			<SidebarLink to={to}/>
		</MenuItem>
	);
};

const Sidebar = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const [isCollapsed, setIsCollapsed] = useState(false);
	const [selected, setSelected] = useState("Dashboard");
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [profile, setProfile] = useState({});

	const getPlayerSummaries = () => {
		axios.get(
			"https://" + process.env.REACT_APP_REST_API_ID + ".execute-api.us-east-1.amazonaws.com/ProductionStage/GetPlayerSummaries"
		).then(function (response) {
			setProfile(JSON.parse(response.data.body));
			setInfoLoaded(true);
		}).catch(function (error) {
			console.log(error);
		});
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
						return "Offline";
					case 1:
						return "Online";
					case 2:
						return "Busy";
					case 3:
						return "Away";
					case 4:
						return "Snooze";
					case 5:
						return "Looking to trade";
					case 6:
						return "Looking to play"
					default:
						return "Offline";
				}
			} else {
				return "Private";
			}
		}
	}

	if (infoLoaded === false || profile.length === 0) {
		return (
			<Box sx={{
				position: 'absolute', left: '50%', top: '50%',
				transform: 'translate(-50%, -50%)'
			}}>
				<CircularProgress color="success"/>
			</Box>
		)
	}
	return (
		<Box
			sx={{
				"& .pro-sidebar-inner": {
					background: `${colors.steamColors[2]} !important`,
				},
				"& .pro-icon-wrapper": {
					backgroundColor: "transparent !important",
				},
				"& .pro-inner-item": {
					padding: "5px 35px 5px 20px !important",
				},
				"& .pro-inner-item:hover": {
					color: `${colors.steamColors[6]} !important`,
				},
				"& .pro-menu-item.active": {
					color: `${colors.steamColors[6]} !important`,
				}
			}}
		>
			<ProSidebar collapsed={isCollapsed} width="100%">
				<Menu iconShape="square">
					{/* LOGO AND MENU ICON */}
					<MenuItem
						onClick={() => setIsCollapsed(!isCollapsed)}
						icon={isCollapsed ? <MenuOutlinedIcon/> : undefined}
						style={{
							margin: "10px 0 20px 0",
							color: colors.grey[100]
						}}
					>
						{!isCollapsed && (
							<Box
								display="flex"
								justifyContent="space-between"
								alignItems="center"
								marginLeft="15px"
							>
								<Box style={{marginRight: "55px"}}>
									<Typography variant="h3" color="custom.steamColorD">
										Status: {infoLoaded && definePersonaState()}
									</Typography>
									<Typography variant="h5" color="custom.steamColorE">
										{infoLoaded && formatLastTimeOnlineData()}
									</Typography>
								</Box>
								<IconButton onClick={() => setIsCollapsed(!isCollapsed)}>
									<MenuOutlinedIcon sx={{ color: "custom.steamColorD" }}/>
								</IconButton>
							</Box>
						)}
					</MenuItem>

					{!isCollapsed && (
						<Box mb="25px">
							<Box display="flex" justifyContent="center" alignItems="center">
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
										style={{ cursor: "pointer", borderRadius: "50%" }}
									/>
								</ProfileLink>}
							</Box>
							<Box textAlign="center">
								<Typography
									variant="h2"
									color="primary.main"
									fontWeight="bold"
									sx={{ m: "10px 0 0 0" }}
								>
									{infoLoaded && profile.response.players[0].personaname}
								</Typography>
								<Typography variant="h5" color="custom.steamColorE">
									Steam ID: {infoLoaded && profile.response.players[0].steamid}
								</Typography>
							</Box>
						</Box>
					)}

					{/* MENU ITEMS */}
					<Box paddingLeft={isCollapsed ? undefined : "10%"}>
						<Item
							title="Dashboard"
							to="/"
							icon={<HomeOutlinedIcon/>}
							selected={selected}
							setSelected={setSelected}
						/>

						<Typography
							variant="h6"
							color={colors.grey[300]}
							margin={"15px 0 5px 20px"}
						>
							Data
						</Typography>
						<Item
							title="Friends"
							to="/friends"
							icon={<PeopleOutlinedIcon/>}
							selected={selected}
							setSelected={setSelected}
						/>
						<Item
							title="Contacts Information"
							to="/contacts"
							icon={<ContactsOutlinedIcon/>}
							selected={selected}
							setSelected={setSelected}
						/>
						<Item
							title="Invoices Balances"
							to="/invoices"
							icon={<ReceiptOutlinedIcon/>}
							selected={selected}
							setSelected={setSelected}
						/>

						<Typography
							variant="h6"
							color={colors.grey[300]}
							sx={{ m: "15px 0 5px 20px" }}
						>
							Pages
						</Typography>
						<Item
							title="Profile Form"
							to="/form"
							icon={<PersonOutlinedIcon/>}
							selected={selected}
							setSelected={setSelected}
						/>
						<Item
							title="Calendar"
							to="/calendar"
							icon={<CalendarTodayOutlinedIcon/>}
							selected={selected}
							setSelected={setSelected}
						/>
						<Item
							title="FAQ Page"
							to="/faq"
							icon={<HelpOutlineOutlinedIcon/>}
							selected={selected}
							setSelected={setSelected}
						/>

						<Typography
							variant="h6"
							color={colors.grey[300]}
							sx={{ m: "15px 0 5px 20px" }}
						>
							Statistics
						</Typography>
						<Item
							title="Weapons"
							to="/weapons-stats"
							icon={<BarChartOutlinedIcon/>}
							selected={selected}
							setSelected={setSelected}
						/>
						<Item
							title="Index Chart"
							to="/bar"
							icon={<BarChartOutlinedIcon/>}
							selected={selected}
							setSelected={setSelected}
						/>
						<Item
							title="Pie Chart"
							to="/pie"
							icon={<PieChartOutlineOutlinedIcon/>}
							selected={selected}
							setSelected={setSelected}
						/>
						<Item
							title="Line Chart"
							to="/line"
							icon={<TimelineOutlinedIcon/>}
							selected={selected}
							setSelected={setSelected}
						/>
						<Item
							title="Geography Chart"
							to="/geography"
							icon={<MapOutlinedIcon/>}
							selected={selected}
							setSelected={setSelected}
						/>
					</Box>
				</Menu>
			</ProSidebar>
		</Box>
	);
};

export default Sidebar;