import { Box, Typography, useTheme } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { tokens } from "../../theme";
import { mockDataInvoices } from "../../data/mockData";
import Header from "../../components/Header";
import { motion } from "framer-motion";

const Invoices = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const columns = [
		{ field: "id", headerName: "ID" },
		{
			field: "name",
			headerName: "Name",
			flex: 1,
			cellClassName: "name-column--cell",
		},
		{
			field: "phone",
			headerName: "Phone Number",
			flex: 1,
		},
		{
			field: "email",
			headerName: "Email",
			flex: 1,
		},
		{
			field: "cost",
			headerName: "Cost",
			flex: 1,
			renderCell: (params) => (
				<Typography color={colors.greenAccent[500]}>
					${params.row.cost}
				</Typography>
			),
		},
		{
			field: "date",
			headerName: "Date",
			flex: 1,
		},
	];

	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box margin="20px">
				<Header title="INVOICES" subtitle="List of Invoice Balances"/>
				<Box
					margin="40px 0 0 0"
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
						}
					}}
				>
					<DataGrid checkboxSelection rows={mockDataInvoices} columns={columns}/>
				</Box>
			</Box>
		</motion.div>
	);
};

export default Invoices;