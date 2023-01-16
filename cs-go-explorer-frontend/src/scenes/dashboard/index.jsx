import { Box, Button, IconButton, Typography, useTheme } from "@mui/material";
import { tokens } from "../../theme";
import { mockTransactions } from "../../data/mockData";
import DownloadOutlinedIcon from "@mui/icons-material/DownloadOutlined";
import EmailIcon from "@mui/icons-material/Email";
import PointOfSaleIcon from "@mui/icons-material/PointOfSale";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import TrafficIcon from "@mui/icons-material/Traffic";
import Header from "../../components/Header";
import LineChart from "../../components/LineChart";
import GeographyChart from "../../components/GeographyChart";
import BarChart from "../../components/BarChart";
import StatBox from "../../components/StatBox";
import ProgressCircle from "../../components/ProgressCircle";

const Dashboard = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);

	return (
		<Box m="20px">
			{/* HEADER */}
			{/*<Box display="flex" justifyContent="space-between" alignItems="center">*/}
			{/*	<Header title="DASHBOARD" subtitle="Welcome to your dashboard"/>*/}
			{/*	<Box>*/}
			{/*		<Button*/}
			{/*			sx={{*/}
			{/*				backgroundColor: "custom.steamColorA",*/}
			{/*				color: "custom.steamColorD",*/}
			{/*				fontSize: "14px",*/}
			{/*				fontWeight: "bold",*/}
			{/*				padding: "10px 20px",*/}
			{/*			}}*/}
			{/*			onClick={() => {*/}
			{/*				alert('Download Reports');*/}
			{/*			}}*/}
			{/*		>*/}
			{/*			<DownloadOutlinedIcon sx={{ mr: "10px" }}/>*/}
			{/*			Download Reports*/}
			{/*		</Button>*/}
			{/*	</Box>*/}
			{/*</Box>*/}

			{/*/!* GRID & CHARTS *!/*/}
			{/*<Box*/}
			{/*	display="grid"*/}
			{/*	gridTemplateColumns="repeat(12, 1fr)"*/}
			{/*	gridAutoRows="140px"*/}
			{/*	gap="20px"*/}
			{/*>*/}
			{/*	/!* ROW 1 *!/*/}
			{/*	<Box*/}
			{/*		gridColumn="span 3"*/}
			{/*		backgroundColor="custom.steamColorB"*/}
			{/*		display="flex"*/}
			{/*		alignItems="center"*/}
			{/*		justifyContent="center"*/}
			{/*	>*/}
			{/*		<StatBox*/}
			{/*			title="12,361"*/}
			{/*			subtitle="Emails Sent"*/}
			{/*			progress="0.75"*/}
			{/*			increase="+14%"*/}
			{/*			icon={*/}
			{/*				<EmailIcon*/}
			{/*					sx={{ color: "custom.steamColorF", fontSize: "26px" }}*/}
			{/*				/>*/}
			{/*			}*/}
			{/*		/>*/}
			{/*	</Box>*/}
			{/*	<Box*/}
			{/*		gridColumn="span 3"*/}
			{/*		backgroundColor="custom.steamColorB"*/}
			{/*		display="flex"*/}
			{/*		alignItems="center"*/}
			{/*		justifyContent="center"*/}
			{/*	>*/}
			{/*		<StatBox*/}
			{/*			title="431,225"*/}
			{/*			subtitle="Sales Obtained"*/}
			{/*			progress="0.50"*/}
			{/*			increase="+21%"*/}
			{/*			icon={*/}
			{/*				<PointOfSaleIcon*/}
			{/*					sx={{ color: "custom.steamColorF", fontSize: "26px" }}*/}
			{/*				/>*/}
			{/*			}*/}
			{/*		/>*/}
			{/*	</Box>*/}
			{/*	<Box*/}
			{/*		gridColumn="span 3"*/}
			{/*		backgroundColor="custom.steamColorB"*/}
			{/*		display="flex"*/}
			{/*		alignItems="center"*/}
			{/*		justifyContent="center"*/}
			{/*	>*/}
			{/*		<StatBox*/}
			{/*			title="32,441"*/}
			{/*			subtitle="New Clients"*/}
			{/*			progress="0.30"*/}
			{/*			increase="+5%"*/}
			{/*			icon={*/}
			{/*				<PersonAddIcon*/}
			{/*					sx={{ color: "custom.steamColorF", fontSize: "26px" }}*/}
			{/*				/>*/}
			{/*			}*/}
			{/*		/>*/}
			{/*	</Box>*/}
			{/*	<Box*/}
			{/*		gridColumn="span 3"*/}
			{/*		backgroundColor="custom.steamColorB"*/}
			{/*		display="flex"*/}
			{/*		alignItems="center"*/}
			{/*		justifyContent="center"*/}
			{/*	>*/}
			{/*		<StatBox*/}
			{/*			title="1,325,134"*/}
			{/*			subtitle="Traffic Received"*/}
			{/*			progress="0.80"*/}
			{/*			increase="+43%"*/}
			{/*			icon={*/}
			{/*				<TrafficIcon*/}
			{/*					sx={{ color: "custom.steamColorF", fontSize: "26px" }}*/}
			{/*				/>*/}
			{/*			}*/}
			{/*		/>*/}
			{/*	</Box>*/}

			{/*	/!* ROW 2 *!/*/}
			{/*	<Box*/}
			{/*		gridColumn="span 8"*/}
			{/*		gridRow="span 2"*/}
			{/*		backgroundColor="custom.steamColorB"*/}
			{/*	>*/}
			{/*		<Box*/}
			{/*			mt="25px"*/}
			{/*			p="0 30px"*/}
			{/*			display="flex "*/}
			{/*			justifyContent="space-between"*/}
			{/*			alignItems="center"*/}
			{/*		>*/}
			{/*			<Box>*/}
			{/*				<Typography*/}
			{/*					variant="h5"*/}
			{/*					fontWeight="600"*/}
			{/*					color="custom.steamColorD"*/}
			{/*				>*/}
			{/*					Revenue Generated*/}
			{/*				</Typography>*/}
			{/*				<Typography*/}
			{/*					variant="h3"*/}
			{/*					fontWeight="bold"*/}
			{/*					color="custom.steamColorF"*/}
			{/*				>*/}
			{/*					$59,342.32*/}
			{/*				</Typography>*/}
			{/*			</Box>*/}
			{/*			<Box>*/}
			{/*				<IconButton>*/}
			{/*					<DownloadOutlinedIcon*/}
			{/*						sx={{ fontSize: "26px", color: "custom.steamColorF" }}*/}
			{/*					/>*/}
			{/*				</IconButton>*/}
			{/*			</Box>*/}
			{/*		</Box>*/}
			{/*		<Box height="250px" m="-20px 0 0 0">*/}
			{/*			<LineChart isDashboard={true} />*/}
			{/*		</Box>*/}
			{/*	</Box>*/}
			{/*	<Box*/}
			{/*		gridColumn="span 4"*/}
			{/*		gridRow="span 2"*/}
			{/*		backgroundColor="custom.steamColorB"*/}
			{/*		overflow="auto"*/}
			{/*	>*/}
			{/*		<Box*/}
			{/*			display="flex"*/}
			{/*			justifyContent="space-between"*/}
			{/*			alignItems="center"*/}
			{/*			borderBottom={`4px solid ${colors.steamColors[6]}`}*/}
			{/*			colors={colors.grey[100]}*/}
			{/*			p="15px"*/}
			{/*		>*/}
			{/*			<Typography color="custom.steamColorD" variant="h5" fontWeight="600">*/}
			{/*				Recent Transactions*/}
			{/*			</Typography>*/}
			{/*		</Box>*/}
			{/*		{mockTransactions.map((transaction, i) => (*/}
			{/*			<Box*/}
			{/*				key={`${transaction.txId}-${i}`}*/}
			{/*				display="flex"*/}
			{/*				justifyContent="space-between"*/}
			{/*				alignItems="center"*/}
			{/*				borderBottom={`4px solid ${colors.steamColors[5]}`}*/}
			{/*				p="15px"*/}
			{/*			>*/}
			{/*				<Box>*/}
			{/*					<Typography*/}
			{/*						color="custom.steamColorF"*/}
			{/*						variant="h5"*/}
			{/*						fontWeight="600"*/}
			{/*					>*/}
			{/*						{transaction.txId}*/}
			{/*					</Typography>*/}
			{/*					<Typography color="custom.steamColorD">*/}
			{/*						{transaction.user}*/}
			{/*					</Typography>*/}
			{/*				</Box>*/}
			{/*				<Box color="custom.steamColorD">{transaction.date}</Box>*/}
			{/*				<Box*/}
			{/*					backgroundColor="custom.steamColorF"*/}
			{/*					padding="5px 10px"*/}
			{/*					borderRadius="5px"*/}
			{/*				>*/}
			{/*					${transaction.cost}*/}
			{/*				</Box>*/}
			{/*			</Box>*/}
			{/*		))}*/}
			{/*	</Box>*/}

			{/*	/!* ROW 3 *!/*/}
			{/*	<Box*/}
			{/*		gridColumn="span 4"*/}
			{/*		gridRow="span 2"*/}
			{/*		backgroundColor="custom.steamColorB"*/}
			{/*		p="30px"*/}
			{/*	>*/}
			{/*		<Typography variant="h5" fontWeight="600" color="custom.steamColorD">*/}
			{/*			Campaign*/}
			{/*		</Typography>*/}
			{/*		<Box*/}
			{/*			display="flex"*/}
			{/*			flexDirection="column"*/}
			{/*			alignItems="center"*/}
			{/*			mt="25px"*/}
			{/*		>*/}
			{/*			<ProgressCircle size="125" />*/}
			{/*			<Typography*/}
			{/*				variant="h5"*/}
			{/*				color="custom.steamColorF"*/}
			{/*				marginTop="15px"*/}
			{/*			>*/}
			{/*				$48,352 revenue generated*/}
			{/*			</Typography>*/}
			{/*			<Typography color="custom.steamColorD">*/}
			{/*				Includes extra misc expenditures and costs*/}
			{/*			</Typography>*/}
			{/*		</Box>*/}
			{/*	</Box>*/}
			{/*	<Box*/}
			{/*		gridColumn="span 4"*/}
			{/*		gridRow="span 2"*/}
			{/*		backgroundColor="custom.steamColorB"*/}
			{/*	>*/}
			{/*		<Typography*/}
			{/*			variant="h5"*/}
			{/*			fontWeight="600"*/}
			{/*			color="custom.steamColorD"*/}
			{/*			padding="30px 30px 0 30px"*/}
			{/*		>*/}
			{/*			Sales Quantity*/}
			{/*		</Typography>*/}
			{/*		<Box height="250px" mt="-20px">*/}
			{/*			<BarChart isDashboard={true}/>*/}
			{/*		</Box>*/}
			{/*	</Box>*/}
			{/*	<Box*/}
			{/*		gridColumn="span 4"*/}
			{/*		gridRow="span 2"*/}
			{/*		backgroundColor="custom.steamColorB"*/}
			{/*		padding="30px"*/}
			{/*	>*/}
			{/*		<Typography*/}
			{/*			variant="h5"*/}
			{/*			fontWeight="600"*/}
			{/*			color="custom.steamColorD"*/}
			{/*			marginBottom="15px"*/}
			{/*		>*/}
			{/*			Geography Based Traffic*/}
			{/*		</Typography>*/}
			{/*		<Box height="200px">*/}
			{/*			<GeographyChart isDashboard={true} />*/}
			{/*		</Box>*/}
			{/*	</Box>*/}
			{/*</Box>*/}
		</Box>
	);
};

export default Dashboard;