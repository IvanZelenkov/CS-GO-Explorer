import { useState, useEffect } from "react";
import { ProSidebar, Menu, MenuItem } from "react-pro-sidebar";
import { Box, IconButton, Typography, Link as ProfileLink, useTheme } from "@mui/material";
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
// import Spinner from "react-spinkit";

const Item = ({ title, to, icon, selected, setSelected }) => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	return (
		<MenuItem
			active={selected === title}
			style={{
				color: colors.grey[100]
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

	const sendRequest = () => {
		axios.get(
			"https://" + process.env.REACT_APP_REST_API_ID + ".execute-api.us-east-1.amazonaws.com/ProductionStage/GetPlayerSummaries"
		).then(function (response) {
			setProfile(JSON.parse(response.data.body));
			setInfoLoaded(true);
			// console.log(response.data.getPlayerSummariesBody);
		}).catch(function (error) {
			console.log(error);
		});
	}

	useEffect(() => {
		sendRequest();
	}, []);

	// if (infoLoaded === false) {
	// 	return (
	// 		<>
	// 			<Spinner name="double-bounce" style={{ width: 100, height: 100 }} />
	// 		</>
	// 	)
	// }

	const computeLastTimeOnline = () => {
		if (infoLoaded && profile.length !== 0) {
			let unix_timestamp = profile.response.players[0].lastlogoff;
			let date = new Date(unix_timestamp * 1000);
			let day = date.getDate();
			let month = date.getMonth() + 1;
			let year = date.getFullYear();
			let hours = date.getHours();
			let minutes = "0" + date.getMinutes();
			let seconds = "0" + date.getSeconds();

			// Will display in "mm/dd/yyyy - 10:30:23" format
			return month + "/" + day + "/" + year + " - " + hours + ':' + minutes.substr(-2) + ':' + seconds.substring(-2);
		}
	}

	const definePersonaState = () => {
		if (infoLoaded && profile.length !== 0) {
			let stateNumber = profile.response.players[0].personastate;
			let communityvisibilitystate = profile.response.players[0].communityvisibilitystate;
			if (communityvisibilitystate === 3) {
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

	return (
		<Box
			sx={{
				"& .pro-sidebar-inner": {
					background: `${colors.primary[400]} !important`,
				},
				"& .pro-icon-wrapper": {
					backgroundColor: "transparent !important",
				},
				"& .pro-inner-item": {
					padding: "5px 35px 5px 20px !important",
				},
				"& .pro-inner-item:hover": {
					color: "#868dfb !important",
				},
				"& .pro-menu-item.active": {
					color: "#6870fa !important",
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
							color: colors.grey[100],
						}}
					>
						{!isCollapsed && (
							<Box
								display="flex"
								justifyContent="space-between"
								alignItems="center"
								ml="15px"
							>
								<Box>
									<Typography variant="h3" color={colors.grey[100]}>
										Status: {infoLoaded && definePersonaState()}
									</Typography>
									<Typography variant="h5" color={colors.grey[100]}>
										{infoLoaded && computeLastTimeOnline()}
									</Typography>
								</Box>
								<IconButton onClick={() => setIsCollapsed(!isCollapsed)}>
									<MenuOutlinedIcon />
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
									component="button"
								>
									<Box
										component="img"
										alt="profile-user"
										width="100px"
										height="100px"
										src={profile.response.players[0].avatarfull}
										style={{ cursor: "pointer", borderRadius: "50%" }}
									/>}
								</ProfileLink>}
							</Box>
							<Box textAlign="center">
								<Typography
									variant="h2"
									color={colors.grey[100]}
									fontWeight="bold"
									sx={{ m: "10px 0 0 0" }}
								>
									{infoLoaded && profile.response.players[0].personaname}
								</Typography>
								<Typography variant="h5" color={colors.greenAccent[500]}>
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
							sx={{ m: "15px 0 5px 20px" }}
						>
							Data
						</Typography>
						<Item
							title="Manage Team"
							to="/team"
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
							Charts
						</Typography>
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