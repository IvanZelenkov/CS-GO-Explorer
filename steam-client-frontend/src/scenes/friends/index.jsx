import { Box, Typography, useTheme } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { tokens } from "../../theme";
import AdminPanelSettingsOutlinedIcon from "@mui/icons-material/AdminPanelSettingsOutlined";
import LockOpenOutlinedIcon from "@mui/icons-material/LockOpenOutlined";
import SecurityOutlinedIcon from "@mui/icons-material/SecurityOutlined";
import Header from "../../components/Header";
import axios from "axios";
import { useEffect, useState } from "react";

const Friends = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [friendsList, setFriendsList] = useState({});
	const [profile, setProfile] = useState({});

	useEffect(() => {
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
		getFriendList();
	}, []);

	const formatFriendSinceData = ({ value }) => {
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
		return  <span>{month + "/" + day + "/" + year + " - " + hours + ':' + minutes + ':' + seconds}</span>
	}

	const getPlayerSummaries = ({ value }) => {
		return value;
	}

	// const getPlayerSummaries = () => {
	// 	axios.post(
	// 		"https://" + process.env.REACT_APP_REST_API_ID + ".execute-api.us-east-1.amazonaws.com/ProductionStage/GetPlayerSummaries"
	// 	).then(function (response) {
	// 		setProfile(JSON.parse(response.data.body));
	// 		setInfoLoaded(true);
	// 		// console.log(response.data.getPlayerSummariesBody);
	// 	}).catch(function (error) {
	// 		console.log(error);
	// 	});
	// }

	const columns = [
		{
			field: "steamid",
			headerName: "Steam ID",
			flex: 1,
			renderCell: (value) => {
				return (
					<Box>
						<Typography component={'span'}>
							{getPlayerSummaries(value)}
						</Typography>
					</Box>
				);
			}
		},
		{
			field: "relationship",
			headerName: "Relationship",
			flex: 1,
			cellClassName: "name-column--cell",
		},
		{
			field: "friend_since",
			headerName: "Friend Since",
			type: "number",
			headerAlign: "left",
			align: "left",
			flex: 1,
			renderCell: (value) => {
				return (
					<Box>
						<Typography component={'span'}>
							{formatFriendSinceData(value)}
						</Typography>
					</Box>
				);
			}
		},
		// {
		// 	field: "accessLevel",
		// 	headerName: "Access Level",
		// 	flex: 1,
		// 	renderCell: ({ row: { access } }) => {
		// 		return (
		// 			<Box
		// 				width="60%"
		// 				margin="0 auto"
		// 				padding="5px"
		// 				display="flex"
		// 				justifyContent="center"
		// 				backgroundColor={
		// 					access === "admin"
		// 						? colors.greenAccent[600]
		// 						: access === "manager"
		// 							? colors.greenAccent[700]
		// 							: colors.greenAccent[700]
		// 				}
		// 				borderRadius="4px"
		// 			>
		// 				{access === "admin" && <AdminPanelSettingsOutlinedIcon/>}
		// 				{access === "manager" && <SecurityOutlinedIcon/>}
		// 				{access === "user" && <LockOpenOutlinedIcon/>}
		// 				<Typography color={colors.grey[100]} sx={{ marginLeft: "5px" }}>
		// 					{access}
		// 				</Typography>
		// 			</Box>
		// 		);
		// 	},
		// }
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
					rows={friendsList.friendslist.friends}
					columns={columns}
					getRowId={(row => row.steamid)}
				/>}
			</Box>
		</Box>
	);
};

export default Friends;