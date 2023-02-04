import { Typography, Box } from '@mui/material';

const Header = ({ title, subtitle }) => {

	return (
		<Box marginBottom="2.5vh">
			<Typography
				fontSize="2.5vh"
				color="custom.steamColorD"
				fontWeight="bold"
			>
				{title}
			</Typography>
			<Typography fontSize="1.5vh" color="custom.steamColorF" fontWeight="bold">
				{subtitle}
			</Typography>
		</Box>
	);
}

export default Header;