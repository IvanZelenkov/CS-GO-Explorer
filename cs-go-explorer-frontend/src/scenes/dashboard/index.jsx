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

					</Box>
				</Box>
			</Box>
		</motion.div>
	);
};

export default Dashboard;