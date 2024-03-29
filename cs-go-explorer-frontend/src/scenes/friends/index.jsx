import { useEffect, useState } from "react";
import { Box, Button, Link as ProfileLink, useTheme } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import Refresh from "@mui/icons-material/Refresh";
import axios from "axios";
import states from 'us-state-converter';
import { motion } from "framer-motion";
import { tokens } from "../../theme";
import Header from "../../components/Header";
import UseAnimations from 'react-useanimations';
import loading from 'react-useanimations/lib/loading';
import Loader from "../../components/Loader";

const Friends = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [friendsList, setFriendsList] = useState({});

	const getFriendList = async () => {
		try {
			const response = await axios.get(
				"https://" + process.env.REACT_APP_REST_API_ID + ".execute-api.us-east-1.amazonaws.com/ProductionStage/GetFriendList?steamid="
				+ JSON.parse(localStorage.getItem("steam_id"))
			);
			setFriendsList(JSON.parse(response.data.body));
			setInfoLoaded(true);
		} catch (error) {
			console.log(error);
		}
	}

	useEffect(() => {
		getFriendList();
	}, []);

	const definePersonaState = (personastate, communityvisibilitystate) => {
		if (communityvisibilitystate === 3) {
			switch (personastate) {
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

	const formatLastTimeOnlineData = (lastlogoff, personastate, communityvisibilitystate) => {
		let state = definePersonaState(personastate, communityvisibilitystate);
		if (state === "Online")
			return "Currently online";
		else
			return unixTimeTimestampConverter(lastlogoff);
	}

	const regionNames = new Intl.DisplayNames(
		['en'], { type: 'region' }
	);

	const columns = [
		{
			field: "avatar",
			headerName: "Avatar",
			flex: 1,
			headerAlign: "center",
			align: "center",
			// cellClassName: "name-column--cell",
			renderCell: ({ row }) => {
				if (!row.profileurl || !row.avatarfull)
					return "";
				else
					return (
						<Box display="flex" justifyContent="center" alignItems="center">
							<ProfileLink
								href={row.profileurl}
								target="_blank"
								underline="none"
							>
								<Box
									component="img"
									alt="profile-user"
									width="3vw"
									height="5.5vh"
									src={row.avatarfull}
									style={{ cursor: "pointer", borderRadius: "10%" }}
								/>
							</ProfileLink>
						</Box>
					);
			}
		},
		{
			field: "personaname",
			headerName: "Nickname",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ row }) => {
				if (!row.personaname)
					return "";
				else
					return (
						<Box sx={{ fontSize: "1.2vh" }}>
							{row.personaname}
						</Box>
					);
			}
		},
		{
			field: "steamid",
			headerName: "Steam ID",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ row }) => {
				if (!row.steamid)
					return "";
				else
					return (
						<Box sx={{ fontSize: "1.2vh" }}>
							{row.steamid}
						</Box>
					);
			}
		},
		{
			field: "communityvisibilitystate",
			headerName: "Status",
			flex: 1,
			headerAlign: "center",
			align: "center",
			// type: "number",
			renderCell: ({ row }) => {
				if (!row.personastate || !row.communityvisibilitystate)
					return "";
				else
					return (
						<Box sx={{ fontSize: "1.2vh" }}>
							{definePersonaState(row.personastate, row.communityvisibilitystate)}
						</Box>
					);
			}
		},
		{
			field: "lastlogoff",
			headerName: "Last time online",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ row }) => {
				if (!row.lastlogoff || !row.personastate || !row.communityvisibilitystate)
					return "";
				else
					return (
						<Box sx={{ fontSize: "1.2vh" }}>
							{formatLastTimeOnlineData(row.lastlogoff, row.personastate, row.communityvisibilitystate)}
						</Box>
					);
			}
		},
		{
			field: "timecreated",
			headerName: "Account created",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ row }) => {
				if (!row.timecreated)
					return "";
				else
					return (
						<Box sx={{ fontSize: "1.2vh" }}>
							{unixTimeTimestampConverter(row.timecreated)}
						</Box>
					);
			}
		},
		{
			field: "loccountrycode",
			headerName: "Country",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ row }) => {
				if (!row.loccountrycode)
					return "";
				else {
					try {
						return (
							<Box sx={{fontSize: "1.2vh"}}>
								{regionNames.of(row.loccountrycode)}
							</Box>
						);
					} catch (error) {
						return (
							<></>
						);
					}
				}
			}
		},
		{
			field: "locstatecode",
			headerName: "State",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ row }) => {
				if (!row.locstatecode)
					return "";
				else {
					const stateObject = states(row.locstatecode);
					return <Box sx={{ fontSize: "1.2vh" }}>{stateObject.name}</Box>
				}
			}
		}
	];

	if (infoLoaded === false || friendsList === undefined)
		return (
			<Box margin="1.5vh">
				<Header title="Friends" subtitle="Explore information about friends"/>
				<Loader colors={colors}/>
				{/* REFRESH BUTTON */}
				<Box display="flex" justifyContent="space-between" alignItems="center">
					<Button
						sx={{
							backgroundColor: "custom.steamColorC",
							color: "custom.steamColorD",
							fontSize: "1vh",
							fontWeight: "bold",
							fontFamily: "Montserrat",
							padding: "0.8vh 1.2vh",
							border: `0.2vh solid #5ddcff`,
							boxShadow: "0px 0px 10px #5ddcff",
							":hover": {
								backgroundColor: "custom.steamColorB"
							}
						}}
						onClick={() => {
							setInfoLoaded(false);
							getFriendList();
						}}
					>
						<Refresh sx={{ marginRight: "0.5vh" }}/>
						Refresh
					</Button>
				</Box>
			</Box>
		);
	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box margin="1.5vh">
				<Header title="Friends" subtitle="Explore information about friends"/>
				{/* REFRESH BUTTON */}
				<Box display="flex" justifyContent="space-between" alignItems="center">
					<Button
						sx={{
							backgroundColor: "custom.steamColorC",
							color: "custom.steamColorD",
							fontSize: "1vh",
							fontWeight: "bold",
							fontFamily: "Montserrat",
							padding: "0.8vh 1.2vh",
							border: `0.2vh solid #5ddcff`,
							boxShadow: "0px 0px 10px #5ddcff",
							":hover": {
								backgroundColor: "custom.steamColorB"
							}
						}}
						onClick={() => {
							setInfoLoaded(false);
							getFriendList();
						}}
					>
						<Refresh sx={{ marginRight: "0.5vh" }}/>
						Refresh
					</Button>
				</Box>
				<Box
					margin="2vh 0 0 0"
					height="70vh"
					sx={{
						"& .MuiDataGrid-root": {
							border: "none",
						},
						"& .MuiDataGrid-cell": {
							borderBottom: "none",
						},
						"& .name-column--cell": {
							color: "custom.steamColorE",
							textTransform: "capitalize"
						},
						"& .MuiDataGrid-columnHeaders": {
							backgroundColor: colors.steamColors[3],
							borderBottom: "none",
							fontSize: "1.2vh",
							fontFamily: "Montserrat",
							color: colors.steamColors[6]
						},
						"& .MuiDataGrid-virtualScroller": {
							backgroundColor: colors.primary[400],
							fontFamily: "Montserrat",
							color: colors.steamColors[4]
						},
						"& .MuiDataGrid-footerContainer": {
							backgroundColor: colors.steamColors[3],
							borderTop: "none"
						},
						"& .MuiCheckbox-root": {
							color: `${colors.steamColors[6]} !important`
						},
					}}
				>
					{infoLoaded && <DataGrid
						rows={friendsList.response.players}
						getRowId={((row) => row?.steamid)}
						columns={columns}
						rowHeight={100}
					/>}
				</Box>
			</Box>
		</motion.div>
	);
};

export default Friends;