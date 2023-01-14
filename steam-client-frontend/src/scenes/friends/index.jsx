import { Box, Link as ProfileLink, useTheme } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { tokens } from "../../theme";
import Header from "../../components/Header";
import axios from "axios";
import { useEffect, useState } from "react";
import states from 'us-state-converter';

const Friends = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [friendsList, setFriendsList] = useState({});

	const getFriendList = () => {
		axios.get(
			"https://" + process.env.REACT_APP_REST_API_ID + ".execute-api.us-east-1.amazonaws.com/ProductionStage/GetFriendList"
		).then(function (response) {
			setFriendsList(JSON.parse(response.data.body));
			setInfoLoaded(true);
		}).catch(function (error) {
			console.log(error);
		});
	}

	useEffect(() => {
		getFriendList();
	}, []);

	console.log(friendsList.response);

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

	const unixTimeTimestampConverter = (value) => {
		let unix_timestamp = value;
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
		if (state === "Online") {
			return "Currently online"
		} else {
			return unixTimeTimestampConverter(lastlogoff);
		}
	}

	const regionNames = new Intl.DisplayNames(
		['en'], {type: 'region'}
	);

	const columns = [
		{
			field: "avatar",
			headerName: "Avatar",
			flex: 1,
			// cellClassName: "name-column--cell",
			renderCell: ({ row }) => {
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
								width="40px"
								height="40px"
								src={row.avatar}
								style={{ cursor: "pointer", borderRadius: "50%" }}
							/>
						</ProfileLink>
					</Box>
				);
			}
		},
		{
			field: "personaname",
			headerName: "Nickname",
			flex: 1
		},
		{
			field: "steamid",
			headerName: "Steam ID",
			flex: 1
		},
		{
			field: "communityvisibilitystate",
			headerName: "Status",
			flex: 1,
			// type: "number",
			// headerAlign: "left",
			// align: "left",
			renderCell: ({ row }) => {
				return (
					<Box>
						{definePersonaState(row.personastate, row.communityvisibilitystate)}
					</Box>
				);
			}
		},
		{
			field: "lastlogoff",
			headerName: "Last time online",
			flex: 1,
			// type: "number",
			// headerAlign: "left",
			// align: "left",
			renderCell: ({ row }) => {
				return (
					<Box>
						{formatLastTimeOnlineData(row.lastlogoff, row.personastate, row.communityvisibilitystate)}
					</Box>
				);
			}
		},
		{
			field: "timecreated",
			headerName: "Account created",
			flex: 1,
			renderCell: ({ value }) => {
				return (
					<Box>
						{unixTimeTimestampConverter(value)}
					</Box>
				);
			}
		},
		{
			field: "loccountrycode",
			headerName: "Country",
			flex: 1,
			renderCell: ({ value }) => {
				if (value === undefined)
					return "";
				else
					return <Box>{regionNames.of(value)}</Box>
			}
		},
		{
			field: "locstatecode",
			headerName: "State",
			flex: 1,
			renderCell: ({ value }) => {
				if (value === undefined)
					return "";
				else {
					const stateObject = states(value);
					return <Box>{stateObject.name}</Box>
				}
			}
		},
	];

	return (
		<Box m="20px">
			<Header title="FRIENDS" subtitle="Explore information about friends" />
			<Box
				margin="40px 0 0 0"
				height="75vh"
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
						backgroundColor: "custom.steamColorA",
						borderBottom: "none",
					},
					"& .MuiDataGrid-virtualScroller": {
						backgroundColor: colors.primary[400],
					},
					"& .MuiDataGrid-footerContainer": {
						borderTop: "none",
						backgroundColor: "custom.steamColorA",
					},
					"& .MuiCheckbox-root": {
						color: `${colors.greenAccent[200]} !important`,
					},
				}}
			>
				{infoLoaded && <DataGrid
					rows={friendsList.response.players}
					columns={columns}
					getRowId={((row) => row?.steamid)}
				/>}
			</Box>
		</Box>
	);
};

export default Friends;