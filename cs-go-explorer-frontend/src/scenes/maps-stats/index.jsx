import { Box, Button, useTheme } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { tokens } from "../../theme";
import Header from "../../components/Header";
import axios from "axios";
import { useEffect, useState } from "react";
import { CircularProgress } from "@mui/material";
import Refresh from "@mui/icons-material/Refresh";

const MapsStats = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [userStats, setUserStats] = useState({});

	const getUserStats = () => {
		axios.get(
			"https://" + process.env.REACT_APP_REST_API_ID + ".execute-api.us-east-1.amazonaws.com/ProductionStage/GetUserStatsForGame"
		).then(function (response) {
			setUserStats(reformatUserStatsJson(JSON.parse(response.data.body)));
			setInfoLoaded(true);
		}).catch(function (error) {
			console.log(error);
		});
	}

	useEffect(() => {
		getUserStats();
	}, []);

	console.log(1)

	const reformatUserStatsJson = (overallStats) => {
		const maps = ["dust2", "inferno", "nuke", "vertigo", "office", "train", "lake", "assault",
					  "cbble", "italy", "monastery", "safehouse", "shoots", "stmarc"];
		let mapStats = [];
		for (let i = 0; i < maps.length; i++)
			mapStats.push({
				mapName: maps[i],
				totalRounds: "",
				totalWins: ""
			});

		let newJsonUserStats = {
			steamID: overallStats.playerstats.steamID,
			gameName: "CS:GO",
			stats: mapStats
		};

		for (let i = 0; i < overallStats.playerstats.stats.length; i++) {
			let dataItem = overallStats.playerstats.stats[i];
			if (!dataItem.name.includes("total_wins_map") && !dataItem.name.includes("total_rounds_map"))
				continue;

			let map = maps.find((mapName) => dataItem.name.includes(mapName));
			if (map !== undefined) {
				if (dataItem.name.includes("total_rounds_map"))
					newJsonUserStats.stats[maps.indexOf(map)].totalRounds = dataItem.value;
				else if (dataItem.name.includes("total_wins_map"))
					newJsonUserStats.stats[maps.indexOf(map)].totalWins = dataItem.value;
			}
		}
		return newJsonUserStats;
	}

	const columns = [
		{
			field: "mapName",
			headerName: "Map",
			flex: 1,
			renderCell: ({ value }) => {
				return (
					<Box display="flex" justifyContent="center" alignItems="center">
						<Box
							component="img"
							alt={value}
							width="360px"
							height="203px"
							src={require("../../images/maps/" + value + ".webp")}
							style={{ justifyContent: "center", alignItems: "center", fontSize: "20px", borderRadius: "10px" }}
						/>
					</Box>
				);
			},
			headerAlign: "center",
			align: "center"
		},
		{
			field: "totalWins",
			headerName: "Total Wins",
			flex: 1,
			renderCell: ({ value }) => {
				return (
					<Box display="flex" justifyContent="center" alignItems="center" sx={{fontSize: "20px"}}>
						{value}
					</Box>
				);
			},
			headerAlign: "center",
			align: "center"
		},
		{
			field: "totalRounds",
			headerName: "Total Rounds",
			flex: 1,
			renderCell: ({ value }) => {
				return (
					<Box display="flex" justifyContent="center" alignItems="center" sx={{fontSize: "20px"}}>
						{value}
					</Box>
				);
			},
			headerAlign: "center",
			align: "center"
		},
		{
			field: "totalWins/totalRounds",
			headerName: "Round Win Rate",
			flex: 1,
			renderCell: ({ row }) => {
				if (row.totalRounds.length === 0 || row.totalWins.length === 0) {
					return "";
				}
				return (
					<Box display="flex" justifyContent="center" alignItems="center" sx={{fontSize: "20px"}}>
						{(row.totalWins / row.totalRounds * 100).toFixed(2)} %
					</Box>
				);
			},
			headerAlign: "center",
			align: "center"
		}
	];

	// if (infoLoaded === false || userStats.length === 0) {
	// 	return (
	// 		<Box sx={{
	// 			position: 'absolute', left: '50%', top: '50%',
	// 			transform: 'translate(-50%, -50%)'
	// 		}}>
	// 			<CircularProgress color="success"/>
	// 		</Box>
	// 	)
	// }
	return (
		<Box margin="20px">
			<Header title="MAP STATS" subtitle="Explore map stats"/>
			<Box display="flex" justifyContent="space-between" alignItems="center">
				<Box>
					<Button
						sx={{
							backgroundColor: "custom.steamColorA",
							color: "custom.steamColorD",
							fontSize: "14px",
							fontWeight: "bold",
							padding: "10px 20px",
						}}
						onClick={() => {
							setInfoLoaded(false);
							getUserStats();
						}}
					>
						<Refresh sx={{ mr: "10px" }}/>
						Refresh
					</Button>
				</Box>
			</Box>
			<Box
				margin="40px 0 0 0"
				height="70vh"
				sx={{
					"& .MuiDataGrid-root": {
						border: "none"
					},
					"& .MuiDataGrid-cell": {
						borderBottom: "none"
					},
					"& .name-column--cell": {
						color: "custom.steamColorE",
						textTransform: "capitalize"
					},
					"& .MuiDataGrid-columnHeaders": {
						backgroundColor: "custom.steamColorA",
						borderBottom: "none",
						fontSize: "16px"
					},
					"& .MuiDataGrid-virtualScroller": {
						backgroundColor: colors.primary[400],
					},
					"& .MuiDataGrid-footerContainer": {
						borderTop: "none",
						backgroundColor: "custom.steamColorA"
					},
					"& .MuiCheckbox-root": {
						color: `${colors.greenAccent[200]} !important`
					},
				}}
			>
				{infoLoaded && <DataGrid
					rows={userStats.stats}
					columns={columns}
					getRowId={((row) => row?.mapName)}
					rowHeight={250}
				/>}
			</Box>
		</Box>
	);
};

export default MapsStats;