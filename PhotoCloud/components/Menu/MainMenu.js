import React, {useState} from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import Menu from '@mui/material/Menu';
import MenuIcon from '@mui/icons-material/Menu';
import Container from '@mui/material/Container';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import Tooltip from '@mui/material/Tooltip';
import MenuItem from '@mui/material/MenuItem';
import AdbIcon from '@mui/icons-material/Adb';
import * as Utils from '../Utils/Utils.js'

const pages = ['Start Freeing Photos', 'Previously Freed Photos','Learn More'];
const settings = ['Profile', 'Account', 'Logout'];

function MainMenu() {
  const [anchorElNav, setAnchorElNav] = useState(null);
  const [anchorElUser, setAnchorElUser] = useState(null);

  const handleOpenNavMenu = (event) => {
    setAnchorElNav(event.currentTarget);
    
    console.log("OpenNav",event.currentTarget)
  };
  const handleOpenUserMenu = (event) => {
    setAnchorElUser(event.currentTarget);
  };

  const handleCloseNavMenu = (event,page) => {
    setAnchorElNav(null);
       console.log("CloseNav",event,event.currentTarget,page)
  };

  const handleCloseUserMenu = (e) => {
	  let option=e.currentTarget.dataset.option
	  //console.log("Profile Menu",e.currentTarget.dataset, option)
    setAnchorElUser(null);
    
    if(option=="Logout")
    {
		Utils.loadData("Authorization",{query: "logout"})
		
		return;
	}
	
	
    
    
  };

  return (
    <AppBar position="static" sx={{p : "0px", "backgroundColor" : "MidnightBlue"}}>
      <Container maxWidth="xl">
        <Toolbar disableGutters>
         

          <img src="./images/Logo.png" style={{height: "30px", width: "200px" }} />
          
          <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}>
            {pages.map((page) => (
              <Button
                key={page}
                onClick={(e) => {handleCloseNavMenu(e,page)} }
                sx={{ my: 2, color: 'white', display: 'block' }}
              >
                {page}
              </Button>
            ))}
          </Box>

          <Box sx={{flexGrow: 1, display: "flex", justifyContent: 'flex-end'}}>
            <Tooltip title="Open settings">
              <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                <Avatar alt="Username"  />
              </IconButton>
            </Tooltip>
            <Menu
              sx={{ mt: '45px' }}
              id="menu-appbar"
              anchorEl={anchorElUser}
              anchorOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              keepMounted
              transformOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              open={Boolean(anchorElUser)}
              onClose={handleCloseUserMenu}
            >
              {settings.map((setting) => (
                <MenuItem data-option={setting} key={setting} onClick={handleCloseUserMenu}>
                  <Typography textAlign="center">{setting}</Typography>
                </MenuItem>
              ))}
            </Menu>
          </Box>
        </Toolbar>
      </Container>
    </AppBar>
  );
}
export default MainMenu;
