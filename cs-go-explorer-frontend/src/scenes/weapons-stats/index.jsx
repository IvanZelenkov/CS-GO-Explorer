import { Box, Button, useTheme } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { tokens } from "../../theme";
import Header from "../../components/Header";
import axios from "axios";
import { Outlet, Link } from "react-router-dom";
import { useEffect, useState } from "react";
import { CircularProgress } from "@mui/material";
import Refresh from "@mui/icons-material/Refresh";
import BarChartOutlinedIcon from "@mui/icons-material/BarChartOutlined";
import { useLocation } from 'react-router-dom'

const WeaponsStats = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const location = useLocation();
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [userStats, setUserStats] = useState({});
	const [barChartData, setBarChartData] = useState({});

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
		const weapons = ["ak47", "aug", "awp", "bizon", "deagle", "elite", "famas", "fiveseven",
			             "g3sg1", "galilar", "glock", "hegrenade", "hkp2000", "knife", "m4a1", "m249",
						 "mac10", "mag7", "molotov", "mp7", "mp9", "negev", "nova", "p90", "p250",
						 "sawedoff", "scar20", "sg556", "ssg08", "taser", "tec9", "ump45", "xm1014"];
		let weaponStats = [];
		for (let i = 0; i < weapons.length; i++)
			weaponStats.push({
				weaponName: weapons[i],
				totalKills: "",
				totalShots: "",
				totalHits: ""
			});

		let newJsonUserStats = {
			steamID: overallStats.playerstats.steamID,
			gameName: "CS:GO",
			stats: weaponStats
		};

		for (let i = 0; i < overallStats.playerstats.stats.length; i++) {
			let dataItem = overallStats.playerstats.stats[i];
			if (!dataItem.name.includes("total_kills") &&
				!dataItem.name.includes("total_shots") &&
				!dataItem.name.includes("total_hits")) {
				continue;
			}
			let weapon = weapons.find((weaponName) => dataItem.name.includes(weaponName));
			if (weapon !== undefined) {
				if (dataItem.name.includes("total_kills")) {
					newJsonUserStats.stats[weapons.indexOf(weapon)].totalKills = dataItem.value;
					newJsonUserStats.stats[weapons.indexOf(weapon)].value = dataItem.value;
				}
				else if (dataItem.name.includes("total_shots"))
					newJsonUserStats.stats[weapons.indexOf(weapon)].totalShots = dataItem.value;
				else if (dataItem.name.includes("total_hits"))
					newJsonUserStats.stats[weapons.indexOf(weapon)].totalHits = dataItem.value;
			}
		}

		setBarChartData(reformatUserStatsBarChart(newJsonUserStats));

		return newJsonUserStats;
	}

	const reformatUserStatsBarChart = (reformattedUserStats) => {
		let weaponStats = [];

		const weapons = ["ak47", "aug", "awp", "bizon", "deagle", "elite", "famas", "fiveseven",
			"g3sg1", "galilar", "glock", "hegrenade", "hkp2000", "knife", "m4a1", "m249",
			"mac10", "mag7", "molotov", "mp7", "mp9", "negev", "nova", "p90", "p250",
			"sawedoff", "scar20", "sg556", "ssg08", "taser", "tec9", "ump45", "xm1014"];

		for (let i = 0; i < weapons.length; i++) {
			weaponStats.push({
				weaponName: reformattedUserStats.stats[i].weaponName,
				[reformattedUserStats.stats[i].weaponName]: reformattedUserStats.stats[i].totalKills,
				[reformattedUserStats.stats[i].weaponName + "Color"]: "hsl(229, 70%, 50%)"
			});
		}

		return weaponStats;
	}

	const columns = [
		{
			field: "weaponName",
			headerName: "Weapon",
			flex: 1,
			renderCell: ({ value }) => {
				return (
					<Box display="flex" justifyContent="center" alignItems="center">
						<Box
							component="img"
							alt={value}
							width="192px"
							height="144px"
							src={require("../../images/weapons/" + value + ".webp")}
							style={{ justifyContent: "center", alignItems: "center", fontSize: "20px" }}
						/>
					</Box>
				);
			},
			headerAlign: "center",
			align: "center"
		},
		{
			field: "totalKills",
			headerName: "Total Kills",
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
			field: "totalHits",
			headerName: "Total Hits",
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
			field: "totalShots",
			headerName: "Total Shots",
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
			field: "totalHits/totalShots",
			headerName: "Hits/Shots %",
			flex: 1,
			renderCell: ({ row }) => {
				if (row.totalHits.length === 0 || row.totalShots.length === 0) {
					return "";
				}
				else {
					return <Box display="flex" justifyContent="center" alignItems="center" sx={{fontSize: "20px"}}>
						{(row.totalHits / row.totalShots * 100).toFixed(2)} %
					</Box>
				}
			},
			headerAlign: "center",
			align: "center"
		}
	];

	if (infoLoaded === false || userStats.length === 0) {
		return (
			<Box sx={{
				position: 'absolute', left: '50%', top: '50%',
				transform: 'translate(-50%, -50%)'
			}}>
				<CircularProgress color="success"/>
			</Box>
		);
	} else if (location.pathname === "/weapons-stats") {
		return (
			<Box margin="20px">
				<Header title="WEAPONS STATS" subtitle="Explore weapons stats"/>
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
							<Refresh sx={{mr: "10px"}}/>
							Refresh
						</Button>
					</Box>
					<Box>
						<Button
							sx={{
								backgroundColor: "custom.steamColorA",
								color: "custom.steamColorD",
								fontSize: "14px",
								fontWeight: "bold",
								padding: "10px 20px",
							}}
							component={Link}
							to={location.pathname + "/bar"}
						>
							<BarChartOutlinedIcon sx={{mr: "10px"}}/>
							Data Visualization
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
						getRowId={((row) => row?.weaponName)}
						rowHeight={150}
					/>}
				</Box>
				<Outlet/>
			</Box>
		);
	} else if (location.pathname === "/weapons-stats/bar") {
		return <Outlet context={[barChartData, setBarChartData]}/>
	}
};

export default WeaponsStats;