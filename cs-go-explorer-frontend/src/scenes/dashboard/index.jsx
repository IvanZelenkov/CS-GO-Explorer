import { Box, Button } from "@mui/material";
import DownloadOutlinedIcon from "@mui/icons-material/DownloadOutlined";
import Header from "../../components/Header";
import { motion } from "framer-motion";

const Dashboard = () => {

	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box margin="1.5vh">
				<Box display="flex" justifyContent="space-between" alignItems="center">
					<Header title="DASHBOARD" subtitle="Welcome to your dashboard"/>
					<Box>
						<Button
							sx={{
								backgroundColor: "custom.steamColorA",
								color: "custom.steamColorD",
								fontSize: "1vh",
								fontWeight: "bold",
								padding: "0.8vh 1.2vh"
							}}
							onClick={() => {
								alert('Download Reports');
							}}
						>
							<DownloadOutlinedIcon sx={{ marginRight: "0.5vh" }}/>
							Download Reports
						</Button>
					</Box>
				</Box>
			</Box>
		</motion.div>
	);
};

export default Dashboard;