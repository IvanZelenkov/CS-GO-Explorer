import { Box, Button } from "@mui/material";
import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import { tokens } from "../../theme";
import Header from "../../components/Header";
import { useTheme } from "@mui/material";
import axios from "axios";
import Refresh from "@mui/icons-material/Refresh";
import { useEffect, useState } from "react";

const Contacts = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [tableData, setTableData] = useState([]);

	const getAllTableItems = () => {
		axios.post(
			"https://" + process.env.REACT_APP_REST_API_ID + ".execute-api.us-east-1.amazonaws.com/ProductionStage/GetAllTableItems",
			JSON.stringify({"ENTIRE_TABLE": "EmptyBody"})
		).then(function (response) {
			setTableData(response.data.body);
			setInfoLoaded(true);
		}).catch(function (error) {
			console.log(error);
		});
	};

	useEffect(() => {
		getAllTableItems();
	}, []);

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
			cellClassName: "name-column--cell",
		},
		{
			field: "email",
			headerName: "Email",
			flex: 1,
		}
	];

	return (
		<Box m="20px">
			<Header
				title="CONTACTS"
				subtitle="List of Contacts for Future Reference"
			/>
			{/* REFRESH BUTTON */}
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
							getAllTableItems();
						}}
					>
						<Refresh sx={{ mr: "10px" }}/>
						Refresh
					</Button>
				</Box>
			</Box>
			<Box
				m="40px 0 0 0"
				height="75vh"
				sx={{
					"& .MuiDataGrid-root": {
						border: "none"
					},
					"& .MuiDataGrid-cell": {
						borderBottom: "none"
					},
					"& .name-column--cell": {
						color: colors.greenAccent[300]
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
						color: `${colors.greenAccent[200]} !important`
					},
					"& .MuiDataGrid-toolbarContainer .MuiButton-text": {
						color: `${colors.grey[100]} !important`
					},
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
	);
};

export default Contacts;