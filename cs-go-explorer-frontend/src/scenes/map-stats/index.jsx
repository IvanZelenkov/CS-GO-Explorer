import { Box, Button, useTheme } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { Link, Outlet, useLocation } from "react-router-dom";
import { useEffect, useState } from "react";
import { CircularProgress } from "@mui/material";
import { motion } from "framer-motion";
import { tokens } from "../../theme";
import Refresh from "@mui/icons-material/Refresh";
import BarChartOutlinedIcon from "@mui/icons-material/BarChartOutlined";
import axios from "axios";
import Header from "../../components/Header";
import SidebarBackgroundImage from "../../images/sidebar/background.jpeg";
import PieChartOutlineOutlinedIcon from "@mui/icons-material/PieChartOutlineOutlined";

const MapStats = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const location = useLocation();
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [userStats, setUserStats] = useState({});
	const [chartData, setChartData] = useState({});
	const [chartKeys, setChartKeys] = useState([]);
	const [chartKeyName, setChartKeyName] = useState("");
	const [chartColors, setChartColors] = useState({});
	const [chartSubtitle, setChartSubtitle] = useState("");
	const maps = ["Dust II", "Inferno", "Nuke", "Vertigo", "Office", "Train", "Lake", "Assault",
		"Cobblestone", "Italy", "Monastery", "Safehouse", "Shoots", "St. Marc"];
	const customColors = { "dust2": "#C0392B", "inferno": "#E74C3C", "nuke": "#9B59B6", "vertigo": "#8E44AD",
		"office": "#2980B9", "train": "#3498DB", "lake": "#1ABC9C", "assault": "#16A085",
		"cbble": "#27AE60", "italy": "#2ECC71", "monastery": "#F1C40F", "safehouse": "#F39C12",
		"shoots": "#D35400", "stmarc": "#ECF0F1" };

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

	const reformatUserStatsJson = (overallStats) => {
		const maps = ["dust2", "inferno", "nuke", "vertigo", "office", "train", "lake", "assault",
					  "cbble", "italy", "monastery", "safehouse", "shoots", "stmarc"];
		setChartKeys(maps);

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
			if (map !== undefined)
				if (dataItem.name.includes("total_rounds_map"))
					newJsonUserStats.stats[maps.indexOf(map)].totalRounds = dataItem.value;
				else if (dataItem.name.includes("total_wins_map"))
					newJsonUserStats.stats[maps.indexOf(map)].totalWins = dataItem.value;
		}
		reformatUserStatsBarChart(newJsonUserStats);
		// reformatUserStatsPieChart(newJsonUserStats);
		return newJsonUserStats;
	}

	const reformatUserStatsBarChart = (reformattedUserStats) => {
		let mapStats = [];
		for (let i = 0; i < maps.length; i++)
			mapStats.push({
				mapName: maps[i],
				[reformattedUserStats.stats[i].mapName]: (reformattedUserStats.stats[i].totalWins /
					reformattedUserStats.stats[i].totalRounds * 100).toFixed(2)
			});

		setChartKeyName("mapName");
		setChartData(mapStats);
		setChartColors(customColors);
		setChartSubtitle("Map win stats");

		return mapStats;
	}

	const reformatUserStatsPieChart = (reformattedUserStats) => {
		let mapStats = [];
		for (let i = 0; i < maps.length; i++)
			mapStats.push({
				id: maps[i],
				value: reformattedUserStats.stats[i].totalWins
			});

		setChartKeyName("mapName");
		setChartData(mapStats);
		setChartColors(customColors);
		setChartSubtitle("Map win stats");

		return mapStats;
	}

	const columns = [
		{
			field: "mapName",
			headerName: "Map",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ value }) => {
				return (
					<Box display="flex" justifyContent="center" alignItems="center">
						<Box
							component="img"
							alt={value}
							width="17vw"
							height="17vh"
							src={require("../../images/maps/" + value + ".webp")}
							style={{ justifyContent: "center", alignItems: "center", fontSize: "1.2vh", borderRadius: "10px" }}
						/>
					</Box>
				);
			}
		},
		{
			field: "totalWins",
			headerName: "Total Wins",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ value }) => {
				return (
					<Box display="flex" justifyContent="center" alignItems="center" sx={{ fontSize: "1.2vh" }}>
						{value}
					</Box>
				);
			}
		},
		{
			field: "totalRounds",
			headerName: "Total Rounds",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ value }) => {
				return (
					<Box display="flex" justifyContent="center" alignItems="center" sx={{ fontSize: "1.2vh" }}>
						{value}
					</Box>
				);
			}
		},
		{
			field: "totalWins/totalRounds",
			headerName: "Round Win Rate",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ row }) => {
				if (row.totalRounds.length === 0 || row.totalWins.length === 0) {
					return "";
				} else {
					return (
						<Box display="flex" justifyContent="center" alignItems="center" sx={{ fontSize: "1.2vh" }}>
							{(row.totalWins / row.totalRounds * 100).toFixed(2)} %
						</Box>
					);
				}
			}
		}
	];

	if (infoLoaded === false || userStats.length === 0) {
		return (
			<Box sx={{ position: 'absolute', left: '50%', top: '50%', transform: 'translate(-50%, -50%)' }}>
				<CircularProgress color="success"/>
			</Box>
		);
	} else if (location.pathname === "/map-stats") {
		return (
			<motion.div exit={{ opacity: 0 }}>
				<Box margin="1.5vh">
					<Header title="MAP STATS" subtitle="Explore map stats"/>
					<Box display="flex" justifyContent="space-between" alignItems="center">
						<Button
							sx={{
								backgroundColor: "custom.steamColorA",
								color: "custom.steamColorD",
								fontSize: "1vh",
								fontWeight: "bold",
								padding: "0.8vh 1.2vh"
							}}
							onClick={() => {
								setInfoLoaded(false);
								getUserStats();
							}}
						>
							<Refresh sx={{ marginRight: "0.5vh" }}/>
							Refresh
						</Button>
						<Box display="flex" flexDirection="row">
							<Button
								sx={{
									backgroundColor: "custom.steamColorA",
									color: "custom.steamColorD",
									fontSize: "1vh",
									fontWeight: "bold",
									padding: "0.8vh 1.2vh",
									marginRight: "2vh"
								}}
								component={Link}
								to={location.pathname + "/bar"}
							>
								<BarChartOutlinedIcon sx={{ marginRight: "0.5vh" }}/>
								NUMBER OF ROUNDS STATS
							</Button>
							<Button
								sx={{
									backgroundColor: "custom.steamColorA",
									color: "custom.steamColorD",
									fontSize: "1vh",
									fontWeight: "bold",
									padding: "0.8vh 1.2vh"
								}}
								component={Link}
								to={location.pathname + "/pie"}
							>
								<PieChartOutlineOutlinedIcon sx={{ marginRight: "0.5vh" }}/>
								MAP WIN STATS
							</Button>
						</Box>
					</Box>
					<Box
						margin="2vh 0 0 0"
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
								backgroundImage: `url(${SidebarBackgroundImage}) !important`,
								backgroundSize: 'cover',
								backgroundRepeat  : 'no-repeat',
								backgroundPosition: 'center',
								borderBottom: "none",
								fontSize: "1.2vh"
							},
							"& .MuiDataGrid-virtualScroller": {
								backgroundColor: colors.primary[400]
							},
							"& .MuiDataGrid-footerContainer": {
								backgroundImage: `url(${SidebarBackgroundImage}) !important`,
								backgroundSize: 'cover',
								backgroundRepeat  : 'no-repeat',
								backgroundPosition: 'center',
								borderTop: "none"
							},
							"& .MuiCheckbox-root": {
								color: `${colors.steamColors[6]} !important`
							}
						}}
					>
						{infoLoaded && <DataGrid
							rows={userStats.stats}
							getRowId={((row) => row?.mapName)}
							columns={columns}
							rowHeight={250}
						/>}
					</Box>
				</Box>
			</motion.div>
		);
	} else if (location.pathname === "/map-stats/bar" || location.pathname === "/map-stats/pie") {
		return (
			<Outlet context={{
				chartData, setChartData,
				chartKeys, setChartKeys,
				chartColors, setChartColors,
				chartKeyName, setChartKeyName,
				chartSubtitle, setChartSubtitle
			}}/>
		);
	}
};

export default MapStats;