import { Box, Button } from "@mui/material";
import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import { tokens } from "../../theme";
import Header from "../../components/Header";
import { useTheme } from "@mui/material";
import axios from "axios";
import Refresh from "@mui/icons-material/Refresh";
import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import loading from "react-useanimations/lib/loading";
import UseAnimations from "react-useanimations";

const PlaytimeBooster = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [tableData, setTableData] = useState([]);

	const getAllTableItems = async () => {
		try {
			await axios.post(
				"https://" + process.env.REACT_APP_REST_API_ID + ".execute-api.us-east-1.amazonaws.com/ProductionStage/GetAllTableItems",
				JSON.stringify({"ENTIRE_TABLE": "EmptyBody"})
			).then(function (response) {
				setTableData(response.data.body);
				setInfoLoaded(true);
			});
		} catch (error) {
			console.log(error);
		}
	};

	// useEffect(() => {
	// 	getAllTableItems();
	// }, []);

	const columns = [
		{
			field: "studentId",
			headerName: "Student ID",
			type: "number",
			flex: 0.5,
			headerAlign: "left",
			align: "left"
		},
		{
			field: "firstName",
			headerName: "First Name",
			flex: 1,
			cellClassName: "name-column--cell"
		},
		{
			field: "lastName",
			headerName: "Last Name",
			flex: 1,
			cellClassName: "name-column--cell"
		},
		{
			field: "classification",
			headerName: "Classification",
			flex: 1
		},
		{
			field: "dateOfBirth",
			headerName: "Date of Birth",
			flex: 1,
			cellClassName: "name-column--cell"
		},
		{
			field: "email",
			headerName: "Email",
			flex: 1
		}
	];

	if (infoLoaded === false || tableData.length === 0) {
		return (
			<motion.div exit={{ opacity: 0 }}>
				<Box margin="1.5vh">
					<Header title="Currently under development"/>
					<Box sx={{ position: 'absolute', left: '50%', top: '50%', transform: 'translate(-50%, -50%)' }}>
						<UseAnimations animation={loading} size={50} fillColor={"#7da10e"} strokeColor={"#7da10e"}/>
					</Box>
				</Box>
			</motion.div>
		);
	}
	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box margin="1.5vh">
				<Header title="Playtime Booster" subtitle="List of Contacts for Future Reference"/>
				{/* REFRESH BUTTON */}
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
							getAllTableItems();
						}}
					>
						<Refresh sx={{ marginRight: "0.5vh" }}/>
						Refresh
					</Button>
				</Box>
				<Box
					margin="2vh 0 0 0"
					height="75vh"
					sx={{
						"& .MuiDataGrid-root": {
							border: "none"
						},
						"& .MuiDataGrid-cell": {
							borderBottom: "none"
						},
						"& .name-column--cell": {
							color: colors.steamColors[6]
						},
						"& .MuiDataGrid-columnHeaders": {
							backgroundColor: "custom.steamColorA",
							borderBottom: "none"
						},
						"& .MuiDataGrid-virtualScroller": {
							backgroundColor: colors.primary[400]
						},
						"& .MuiDataGrid-footerContainer": {
							borderTop: "none",
							backgroundColor: "custom.steamColorA"
						},
						"& .MuiCheckbox-root": {
							color: `${colors.steamColors[6]} !important`
						},
						"& .MuiDataGrid-toolbarContainer .MuiButton-text": {
							color: `${colors.grey[100]} !important`
						}
					}}
				>
					{infoLoaded && tableData.length !== 0 && <DataGrid
						rows={tableData}
						getRowId={(row) => row?.studentID}
						columns={columns}
						components={{ Toolbar: GridToolbar }}
					/>}
				</Box>
			</Box>
		</motion.div>
	);
};

export default PlaytimeBooster;