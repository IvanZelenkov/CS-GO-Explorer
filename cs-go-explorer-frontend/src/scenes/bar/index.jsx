import { Box, Button } from "@mui/material";
import Header from "../../components/Header";
import BarChart from "../../components/BarChart";
import { useNavigate } from "react-router-dom";
import { useOutletContext } from "react-router-dom";
import { motion } from "framer-motion";
import KeyboardBackspaceRoundedIcon from '@mui/icons-material/KeyboardBackspaceRounded';

const Bar = () => {
	let navigation = useNavigate();
	const { chartState, chartSubtitle } = useOutletContext();

	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box margin="1.5vh">
				<Box display="flex" alignItems="center" flexDirection="row">
					<Button
						sx={{
							backgroundColor: "custom.steamColorA",
							color: "custom.steamColorD",
							fontWeight: "bold",
							margin: "0 2.5vh 2.5vh 0",
							width: "2vw",
							height: "3vh"
						}}
						onClick={() => navigation(-1)}
					>
						<KeyboardBackspaceRoundedIcon fontSize="large"/>
					</Button>
					<Header title="Bar Chart" subtitle={chartSubtitle}/>
				</Box>
				<Box height="80vh">
					<BarChart chartState={chartState} chartSubtitle={chartSubtitle}/>
				</Box>
			</Box>
		</motion.div>
	);
};

export default Bar;